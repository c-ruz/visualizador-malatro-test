import { AppBar, Typography } from '@mui/material'

interface TopBarProps {
  message: string
}

function TopBar({ message }: TopBarProps) {
  return (
    <AppBar position="static" sx={{ p: 2, backgroundColor: 'secondary.dark', color: 'secondary.contrastText', placeItems: 'center' }}>
      <Typography variant="h5" component="div">
        {message || 'Choose an action...'}
      </Typography>
    </AppBar>
  )
}

export default TopBar
