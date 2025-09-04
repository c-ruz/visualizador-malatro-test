import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { ThemeProvider } from '@emotion/react'
import { createTheme } from '@mui/material'

// 1. Define the color palettes for light and dark modes
const lightPalette = {
  primary: {
    main: '#1976d2', // A classic, friendly blue
    light: '#42a5f5',
    dark: '#1565c0',
    contrastText: '#fff',
  },
  secondary: {
    main: '#009688', // A vibrant teal for accents
    light: '#4db6ac',
    dark: '#00796b',
    contrastText: '#fff',
  },
  background: {
    default: '#f4f6f8', // A very light grey for the background
    paper: '#ffffff', // White for cards and surfaces
  },
  text: {
    primary: '#2c3e50', // A dark, soft black for text
    secondary: '#7f8c8d',
  },
}

const darkPalette = {
  primary: {
    main: '#42a5f5', // Lighter blue for dark mode
    light: '#64b5f6',
    dark: '#2196f3',
    contrastText: '#000',
  },
  secondary: {
    main: '#4db6ac', // Lighter teal for dark mode
    light: '#80cbc4',
    dark: '#26a69a',
    contrastText: '#000',
  },
  background: {
    default: '#121212', // A standard dark background
    paper: '#1e1e1e', // A slightly lighter dark for surfaces
  },
  text: {
    primary: '#ffffff',
    secondary: '#bdc3c7',
  },
}

// 2. Create a function to generate the theme options
const getThemeOptions = (mode: 'light' | 'dark') => ({
  palette: mode === 'light' ? lightPalette : darkPalette,
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h5: {
      fontWeight: 700,
      letterSpacing: '0.5px',
    },
  },
  components: {
    // Customizing the Card component
    MuiCard: {
      styleOverrides: {
        root: {
          'transition': 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
          '&:hover': {
            transform: 'translateY(-8px)',
            boxShadow: `0 8px 20px -5px ${mode === 'light' ? 'rgba(0,0,0,0.1)' : 'rgba(0,0,0,0.4)'}`,
          },
        },
      },
    },
  },
})

const theme = createTheme(getThemeOptions('light'))

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <App />
    </ThemeProvider>
  </StrictMode>,
)
