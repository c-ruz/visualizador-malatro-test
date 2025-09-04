import { Box, Grow, Stack, Typography } from '@mui/material'
import GameCard from '../GameCard/GameCard'
import type { Card } from '../../types/types'
import { TransitionGroup } from 'react-transition-group'

interface BoardRowProps {
  title: string
  cards: Card[]
}

function BoardRow({ title, cards }: BoardRowProps) {
  return (
    <Stack
      p={1}
      mb={1}
      direction="column"
      borderRadius={2}
      bgcolor="rgba(0, 0, 0, 0.2)"
      spacing={1}
    >
      <Typography variant="body2" sx={{ p: 2, color: 'rgba(255,255,255,0.5)' }}>{title}</Typography>
      <Stack
        p={2}
        direction="row"
        alignItems="flex-start"

        width="100%"
        minHeight={200}

        flexWrap="wrap"
        alignContent="space-between"
      >
        {cards.length > 0
          ? (
              <TransitionGroup component={null}>
                {cards.map(card => (
                  <Grow key={card.id}>
                    <Box mr={1} mb={1}>
                      <GameCard card={card} height={200} />
                    </Box>
                  </Grow>
                ))}
              </TransitionGroup>
            )
          : <Typography variant="body2" sx={{ p: 2, color: 'rgba(255,255,255,0.5)' }}>Empty</Typography>}
      </Stack>
    </Stack>
  )
}

export default BoardRow
