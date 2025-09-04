import { Card as CardMui, CardMedia, CardContent, Typography, Grid, CardActionArea, Tooltip, Zoom, styled, type TooltipProps, tooltipClasses, Stack } from '@mui/material'
import type { Card } from '../../types/types'
import useGameStore from '../../store/GameStateStore'

interface GameCardProps {
  card: Card
  height?: number
  onClick?: () => void
}

const GameCardTooltip = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip {...props} arrow classes={{ popper: className }} />
))(() => ({
  [`& .${tooltipClasses.tooltip}`]: {
    minWidth: 300,
    fontSize: '1em',
  },
}))

function GameCard({ card, height, onClick }: GameCardProps) {
  const { gameStateLoading } = useGameStore()

  const textClamp = {
    overflow: 'hidden',
    whiteSpace: 'nowrap',
    textOverflow: 'ellipsis',
  }

  return (
    <GameCardTooltip
      title={(
        <Grid container spacing={1}>
          <Grid size={12}>
            <Typography>{card.name}</Typography>
          </Grid>
          {card.attributes.map(attr => (
            <>
              <Grid size={3}>
                <Typography>{attr.name}</Typography>
              </Grid>
              <Grid size={9}>
                <Typography>{attr.value}</Typography>
              </Grid>
            </>
          ))}
        </Grid>
      )}
      placement="left-end"
      slots={{ transition: Zoom }}
    >
      <CardMui
        sx={{
          height: height || '92%',
          aspectRatio: '100 / 140',
          color: 'secondaty.contrastText',
          backgroundColor: 'secondary.main',
          border: '0.3em solid',
          borderColor: 'secondary.light',
          flexShrink: 0,
        }}
      >
        <CardActionArea sx={{ height: '100%' }} onClick={onClick} disabled={!onClick || gameStateLoading}>
          <CardMedia
            image={`${card.name.toLowerCase()}.jpg`}
            sx={{ height: '50%' }}
          />
          <CardContent sx={{ p: 1, height: '40%', display: 'flex', flexDirection: 'column', justifyContent: 'flex-end' }}>
            <Typography fontSize="0.8em" textAlign="center" flexGrow={1} sx={textClamp}>
              {card.name}
            </Typography>
            <Stack direction="row" justifyContent="space-between" width="100%">
              {card.attributes.slice(0, 2).map((attr, i) => (
                <Typography
                  fontSize="0.5em"
                  width="50%"
                  textAlign={i === 0 ? 'left' : 'right'}
                  sx={textClamp}
                >
                  {attr.value}
                </Typography>
              ))}
            </Stack>
          </CardContent>
        </CardActionArea>
      </CardMui>
    </GameCardTooltip>
  )
}

export default GameCard
