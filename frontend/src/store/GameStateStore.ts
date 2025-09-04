import { create } from 'zustand'
import type { CardPile, Player, Score } from '../types/types'

export interface GameState {
  players: Player[]
  cardPiles: CardPile[]
  currentPlayerId?: string
  actions: Action[]
  scores: Score[]
  messageToShow?: string
}

export interface Action {
  className: string
  params: {
    name: string
  }
}

type ActionMessage = { message: string } | { error: string }

interface GameStore {
  gameStateLoading: boolean
  gameState: GameState
  actionMessage: { message: string, severity: 'success' | 'error' }
  showActionMessage: boolean
  fetchGameState: () => Promise<void>
  doAction: (action: Action) => Promise<void>
  toggleActionMessage: () => void
}

const API_URL = 'http://localhost:8080'

const useGameStore = create<GameStore>()(set => ({
  gameStateLoading: false,
  actionsLoading: false,
  gameState: { players: [], cardPiles: [], actions: [], scores: [] },
  actionMessage: { message: '', severity: 'error' },
  showActionMessage: false,

  fetchGameState: async () => {
    set({ gameStateLoading: true })
    const resGameState = await fetch(`${API_URL}/state`)
    const gameState: Omit<GameState, 'actions'> = await resGameState.json()

    const resActions = await fetch(`${API_URL}/action`)
    const actions: Action[] = await resActions.json()

    set({ gameState: { ...gameState, actions }, gameStateLoading: false })
  },

  doAction: async (action) => {
    set({ gameStateLoading: true })
    const res = await fetch(
      `${API_URL}/action`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(action),
      },
    )
    const actionMessage: ActionMessage = await res.json()

    if ('error' in actionMessage) {
      set({
        actionMessage: { message: actionMessage.error, severity: 'error' },
        showActionMessage: true,
      })
    }
    else {
      set({
        actionMessage: { message: actionMessage.message, severity: 'success' },
        showActionMessage: true,
      })
    }
  },

  toggleActionMessage: () => {
    set({ showActionMessage: false })
  },
}))

export default useGameStore
