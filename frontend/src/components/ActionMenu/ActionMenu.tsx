import { Box, List, ListItemButton, ListItemText, Skeleton } from '@mui/material'
import type { Action } from '../../store/GameStateStore'
import { useEffect, useState } from 'react'
import { ArrowBack } from '@mui/icons-material'
import useGameStore from '../../store/GameStateStore'

interface ActionTree {
  name: string
  path: string
  children?: ActionTree[]
  action?: Action
}

interface ActionMenuItemProps {
  item: ActionTree
  setActionMenu: (am: ActionTree[]) => void
  setHistory: () => void
}

function ActionMenuItem({ item, setActionMenu, setHistory }: ActionMenuItemProps) {
  const { doAction, fetchGameState } = useGameStore()

  const handleClick = async () => {
    if (item.children && item.children.length > 0) {
      setActionMenu(item.children)
      setHistory()
    }
    else if (item.action) {
      await doAction(item.action)
      await fetchGameState()
    }
  }

  return (
    <ListItemButton onClick={handleClick}>
      <ListItemText primary={item.name} />
    </ListItemButton>
  )
}

function ActionMenu() {
  const { gameState: { actions }, gameStateLoading } = useGameStore()

  const [currentMenuLevel, setCurrentMenuLevel] = useState<ActionTree[]>([])
  const [menuHistory, setMenuHistory] = useState<ActionTree[][]>([])
  const [isLoading, setIsLoading] = useState<boolean>(true)

  useEffect(() => {
    setIsLoading(true)
    const root: ActionTree = { name: 'root', path: '', children: [] }

    actions.filter(a => a.className !== 'PlayCard').forEach((action) => {
      const parts = action.params.name.split('/')
      let currentLevel = root.children
      let currentPath = ''

      parts.forEach((part, index) => {
        currentPath = currentPath === '' ? part : `${currentPath}/${part}`
        let existingNode = currentLevel?.find(node => node.name === part)

        if (!existingNode) {
          existingNode = { name: part, path: currentPath }
          if (index === parts.length - 1) {
          // This is a leaf node (an actual action)
            existingNode.action = action
          }
          else {
          // This is a group node
            existingNode.children = []
          }
          currentLevel?.push(existingNode)
        }

        // Move to the next level
        currentLevel = existingNode.children
      })
    })

    setCurrentMenuLevel(root.children || [])
    setMenuHistory([])
    setIsLoading(false)
  }, [actions])

  const handleBack = () => {
    if (menuHistory.length > 0) {
      const previousLevel = menuHistory[menuHistory.length - 1]
      setMenuHistory(prevHistory => prevHistory.slice(0, prevHistory.length - 1))
      setCurrentMenuLevel(previousLevel)
    }
  }

  const addToHistory = () => {
    setMenuHistory(prevHistory => [...prevHistory, currentMenuLevel]) // Push current level to history
  }

  if (isLoading || gameStateLoading) {
    return (
      <Skeleton width="20%" height="100%" variant="rounded" />
    )
  }

  return (
    <Box width="20%" overflow="auto">
      <List component="nav">
        {currentMenuLevel.map(item => (
          <ActionMenuItem
            key={item.path}
            item={item}
            setActionMenu={setCurrentMenuLevel}
            setHistory={addToHistory}
          />
        ))}
        {menuHistory.length > 0 && (
          <ListItemButton onClick={handleBack}>
            <ArrowBack />
            <ListItemText primary="Back" />
          </ListItemButton>
        )}
      </List>
    </Box>
  )
}

export default ActionMenu
