import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Container,
} from '@mui/material';
import {
  Work as WorkIcon,
  School as SchoolIcon,
  QuestionAnswer as InterviewIcon,
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

const Header: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  return (
    <AppBar position="static" elevation={1}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              cursor: 'pointer',
            }}
            onClick={() => navigate('/')}
          >
            <WorkIcon sx={{ mr: 1, fontSize: 32 }} />
            <Typography
              variant="h6"
              noWrap
              sx={{
                fontFamily: 'monospace',
                fontWeight: 700,
                letterSpacing: '.3rem',
                color: 'inherit',
                textDecoration: 'none',
              }}
            >
              Career Coach
            </Typography>
          </Box>

          <Box sx={{ flexGrow: 1, ml: 4 }}>
            <Button
              color="inherit"
              onClick={() => navigate('/')}
              sx={{
                mx: 1,
                backgroundColor: isActive('/') ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
              }}
            >
              대시보드
            </Button>
            <Button
              color="inherit"
              onClick={() => navigate('/resumes')}
              sx={{
                mx: 1,
                backgroundColor: isActive('/resumes') ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
              }}
            >
              이력서 관리
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Button
              color="inherit"
              startIcon={<InterviewIcon />}
              onClick={() => navigate('/resumes/new')}
              sx={{ mr: 1 }}
            >
              이력서 작성
            </Button>
            <Button
              color="inherit"
              startIcon={<SchoolIcon />}
              onClick={() => navigate('/resumes')}
            >
              학습 경로
            </Button>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Header; 