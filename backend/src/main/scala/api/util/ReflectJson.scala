package api.util

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.*

import java.lang.reflect.{Constructor, Field, Modifier, Parameter}
import scala.util.Try

object ReflectJson {

  /** Public entry point */
  def toJson[A](obj: A): JsValue = toJsValue(obj)

  /** Creates an instance of a class described in a JSON string using
    * pekko-http-spray-json.
    *
    * @param jsonString
    *   The JSON string describing the object to create.
    * @return
    *   A `Try` containing the created instance on success, or an exception on
    *   failure.
    */
  def createInstanceFromJson(jsonString: String): Try[Any] = {
    Try {
      val descriptor = jsonString.parseJson.convertTo[ObjectDescriptor](
        JsonProtocol.objectDescriptorFormat
      )
      val classNameWithPackage = "model.actions." + descriptor.className
      val clazz = Class.forName(classNameWithPackage)

      // 2. Find the primary (or first) constructor.
      val constructor = clazz.getDeclaredConstructors.head
      val constructorParams: Array[Parameter] = constructor.getParameters

      // 3. Look up values from the JSON object by the constructor's parameter names.
      // This solves the parameter ordering problem.
      val params: Array[Any] = constructorParams.map { param =>
        val paramName = param.getName
        val paramType = param.getType
        val jsValue = descriptor.params.fields.getOrElse(paramName, JsNull)

        // 4. Convert JsValue to the required Scala type.
        convertJsValue(jsValue, paramType)
      }

      // 5. Call the original createInstance method with the extracted, ordered parameters.
      createInstance(classNameWithPackage, params: _*)
    }.flatten // Flatten the Try[Try[Any]] into Try[Any]
  }

  // ---- Core ----
  private def createInstance(className: String, params: Any*): Try[Any] = {
    Try {
      val clazz = Class.forName(className)
      val constructors = clazz.getDeclaredConstructors
      val paramTypes = params.map {
        case _: Int             => classOf[Int]
        case _: Double          => classOf[Double]
        case _: Boolean         => classOf[Boolean]
        case _: Char            => classOf[Char]
        case _: Byte            => classOf[Byte]
        case _: Short           => classOf[Short]
        case _: Long            => classOf[Long]
        case _: Float           => classOf[Float]
        case obj if obj != null => obj.getClass
        case null               => classOf[AnyRef]
      }

      val matchingConstructor: Option[Constructor[_]] = constructors.find {
        constructor =>
          val constructorParamTypes = constructor.getParameterTypes
          constructorParamTypes.length == paramTypes.length &&
          constructorParamTypes.zip(paramTypes).forall {
            case (ctorParam, providedParam) =>
              ctorParam.isAssignableFrom(
                providedParam
              ) || (providedParam == classOf[AnyRef] && !ctorParam.isPrimitive)
          }
      }

      matchingConstructor match {
        case Some(constructor) =>
          constructor.setAccessible(true)
          val paramsAsAnyRef = params.map(_.asInstanceOf[AnyRef])
          constructor.newInstance(paramsAsAnyRef: _*)
        case None =>
          val paramTypeNames = paramTypes.map(_.getName).mkString(", ")
          throw new NoSuchMethodException(
            s"No constructor found for $className with parameter types: ($paramTypeNames)"
          )
      }
    }
  }

  private def toJsValue(v: Any): JsValue = v match {
    case null                     => JsNull
    case j: JsValue               => j
    case s: String                => JsString(s)
    case b: Boolean               => JsBoolean(b)
    case i: Int                   => JsNumber(BigDecimal(i.toString))
    case l: Long                  => JsNumber(BigDecimal(l))
    case f: Float                 => JsNumber(BigDecimal.decimal(f))
    case d: Double                => JsNumber(BigDecimal(d))
    case bd: BigDecimal           => JsNumber(bd)
    case jd: java.math.BigDecimal => JsNumber(BigDecimal(jd))
    case opt: Option[_]           => opt.map(toJsValue).getOrElse(JsNull)
    case arr: Array[_]            => JsArray(arr.toVector.map(toJsValue))
    case it: Iterable[_]          => JsArray(it.toVector.map(toJsValue))
    case p if isJavaTime(p)       => JsString(p.toString) // ISO-ish
    case other                    => reflectToJsObject(other)
  }

  private case class ObjectDescriptor(className: String, params: JsObject)

  private object JsonProtocol
      extends SprayJsonSupport
      with DefaultJsonProtocol {
    implicit val objectDescriptorFormat: RootJsonFormat[ObjectDescriptor] =
      jsonFormat2(ObjectDescriptor.apply)
  }

  // Reflect all instance fields (including private) up the class hierarchy
  private def reflectToJsObject(obj: Any): JsObject = {
    val cls = obj.getClass
    val fields = allFields(cls)
      .filterNot(isIgnoredField)

    val pairs = fields.flatMap { f =>
      try {
        f.setAccessible(true)
        val name = normalizeFieldName(f.getName)
        val value = f.get(obj)
        Some(name -> toJsValue(value))
      } catch {
        case _: Throwable => None // skip unreadable fields
      }
    }

    JsObject(
      Map("className" -> JsString(cls.getSimpleName)) ++ Map(
        "params" -> JsObject(pairs.toMap)
      )
    )
  }

  // ---- Helpers ----
  private def allFields(c: Class[_]): List[Field] = {
    if (c == null || c == classOf[Object]) Nil
    else c.getDeclaredFields.toList ++ allFields(c.getSuperclass)
  }

  private def isIgnoredField(f: Field): Boolean = {
    val m = f.getModifiers
    Modifier.isStatic(m) ||
    Modifier.isTransient(m) ||
    f.isSynthetic ||
    f.getName.contains("$$") // skip Scala compiler internals
  }

  /** Tidy common Scala compiler field names like "name$1" -> "name" Also
    * removes leading underscores from private fields using Scala's convention.
    */
  private def normalizeFieldName(n: String): String = {
    n.replaceAll("^_+", "").replaceAll("\\$\\d+$", "")
  }

  private def isJavaTime(v: Any): Boolean = {
    v match {
      case _: java.time.temporal.TemporalAccessor => true
      case _                                      => false
    }
  }

  // Helper to convert JsValue to a specific type
  private def convertJsValue(value: JsValue, targetType: Class[_]): Any = {
    (value, targetType) match {
      case (JsString(s), t) if t == classOf[String]   => s
      case (JsNumber(n), t) if t == classOf[Int]      => n.toInt
      case (JsNumber(n), t) if t == classOf[Double]   => n.toDouble
      case (JsNumber(n), t) if t == classOf[Long]     => n.toLong
      case (JsBoolean(b), t) if t == classOf[Boolean] => b
      case (JsNull, _)                                => null
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert $value to type ${targetType.getName}"
        )
    }
  }

  def main(args: Array[String]): Unit = {
    println(
      "--- Attempting to create instances dynamically from JSON using Pekko/Spray ---"
    )

    // --- Test Case 1: Successful creation of PlayCard from JSON ---
    println("\n1. Creating a PlayCard from JSON:")
    val playCardJson =
      """
        {
          "name": "PlayCard",
          "params": {
            "index": 0
          }
        }
      """

    val instance = createInstanceFromJson(playCardJson).get
    println(s"Created instance: $instance of type ${instance.getClass.getName}")
  }
}
