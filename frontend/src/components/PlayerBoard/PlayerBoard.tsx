import { Box } from '@mui/material'
import useGameStore from '../../store/GameStateStore'
import type { Player } from '../../types/types'
import BoardRow from '../BoardRow/BoardRow'

interface PlayerBoardProps {
  player?: Player
}

function PlayerBoard({ player }: PlayerBoardProps) {
  const { gameState: { cardPiles } } = useGameStore()

  const filteredCardPiles = player
    ? cardPiles.filter(pile => pile.ownerId && pile.ownerId === player?.id)
    : cardPiles.filter(pile => !pile.ownerId)

  return (
    <Box sx={{ p: 2 }}>
      {filteredCardPiles.map(pile => (
        <BoardRow key={pile.id} title={pile.id} cards={pile.cards} />
      ))}
    </Box>
  )
}

export default PlayerBoard
