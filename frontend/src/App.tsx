import { useEffect } from 'react'
import useGameStore from './store/GameStateStore'
import { Alert, Divider, Paper, Slide, Snackbar, Stack, Typography } from '@mui/material'
import PlayerBoard from './components/PlayerBoard/PlayerBoard'
import HandOfCards from './components/HandOfCards/HandOfCards'
import ActionMenu from './components/ActionMenu/ActionMenu'
import TopBar from './components/TopBar/TopBar'

function App() {
  const { gameState, fetchGameState, showActionMessage, actionMessage, toggleActionMessage } = useGameStore()

  useEffect(() => {
    fetchGameState()
  }, [])

  const {
    players,
    currentPlayerId,
    scores,
    messageToShow,
  } = gameState

  const currentPlayer = players.find(p => p.id === currentPlayerId)

  return (
    <Stack
      direction="column"
      sx={{ height: '100vh', backgroundColor: 'background.default', color: 'white' }}
    >
      <TopBar message={messageToShow || 'Choose an action'} />
      {/* Action Feedback */}
      <Snackbar
        open={showActionMessage}
        onClose={toggleActionMessage}
        autoHideDuration={5000}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        sx={{ mb: '16vh' }}
        slots={{ transition: Slide }}
      >
        <Alert
          severity={actionMessage.severity}
          variant="filled"
        >
          {actionMessage.message}
        </Alert>
      </Snackbar>

      {/* Gameboard */}
      <Stack direction="column" divider={<Divider sx={{ my: 1, bgcolor: 'background.default' }} />} sx={{ height: '85%', overflowY: 'auto', p: 2 }}>
        {players.map(player => (
          <PlayerBoard key={`board_${player.id}`} player={player} />
        ))}
        <PlayerBoard />
      </Stack>

      {/* 4. Bottom menu: Actions, Hand and Score */}
      <Paper elevation={4} sx={{ height: '15%', p: 2, pt: 3, bgcolor: 'background.paper', color: 'text.primary', borderRadius: 0 }}>
        <Stack
          spacing={1}

          direction="row"
          height="100%"

          alignItems="stretch"
          justifyContent="space-between"
        >
          {/* Action Buttons */}
          <ActionMenu />

          {/* Player Hand */}
          {currentPlayer && (
            <HandOfCards cards={currentPlayer.hand} />
          )}

          {/* Score Display */}
          <Stack width="20%" direction="column" gap={2} justifyContent="flex-start">
            {scores.map((s, i) => (
              <Typography key={`${s.name}_${i}`}>{`${s.name} ${s.value}`}</Typography>
            ))}
          </Stack>
        </Stack>
      </Paper>

    </Stack>
  )
}

export default App
