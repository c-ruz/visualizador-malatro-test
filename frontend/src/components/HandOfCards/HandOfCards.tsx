import { Stack } from '@mui/material'
import useGameStore from '../../store/GameStateStore'
import type { Card } from '../../types/types'
import GameCard from '../GameCard/GameCard'
import { useLayoutEffect, useRef, useState } from 'react'

interface HandOfCardsProps {
  cards: Card[]
  radius?: number
  spreadAngle?: number
}

function HandOfCards({
  cards,
}: HandOfCardsProps) {
  const { gameState: { actions }, doAction, fetchGameState } = useGameStore()
  const playCardActions = actions.filter(a => a.className === 'PlayCard')

  const handRef = useRef<HTMLDivElement>(null)
  const [isOverflowing, setIsOverflowing] = useState(false)

  // Check after render if component is overflowing to set state
  useLayoutEffect(() => {
    const checkOverflow = () => {
      if (handRef.current) {
        setIsOverflowing(handRef.current.scrollWidth > handRef.current.clientWidth)
      }
    }

    checkOverflow()

    window.addEventListener('resize', checkOverflow)
    return () => window.removeEventListener('resize', checkOverflow)
  }, [cards])

  return (
    <Stack
      ref={handRef}

      direction="row"
      p={1}
      spacing={1}

      width="40%"
      justifyContent={isOverflowing ? 'flex-start' : 'center'}

      sx={{ overflow: 'auto' }}
    >
      {cards.map((card, i) => {
        return (
          <GameCard
            key={card.id}
            card={card}
            onClick={async () => {
              await doAction(playCardActions[i])
              await fetchGameState()
            }}
          />
        )
      })}
    </Stack>
  )
}

export default HandOfCards
