import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Card,
  CardContent,
  Typography,
  Button,
  Box,
  Chip,
  LinearProgress,
  List,
  ListItem,
  ListItemText,
  Divider,
  Alert,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {
  ArrowBack as ArrowBackIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import { InterviewQuestionsResponse } from '../types';
import { interviewApi } from '../services/api';

const InterviewQuestions: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [questions, setQuestions] = useState<InterviewQuestionsResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const generateQuestions = useCallback(async () => {
    if (!id) return;
    
    setLoading(true);
    setError('');
    
    try {
      const data = await interviewApi.generateInterviewQuestions(parseInt(id));
      setQuestions(data);
    } catch (error) {
      setError('인터뷰 질문 생성에 실패했습니다.');
      console.error('인터뷰 질문 생성 실패:', error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (id) {
      generateQuestions();
    }
  }, [id, generateQuestions]);

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'JUNIOR':
        return 'success';
      case 'MIDDLE':
        return 'warning';
      case 'SENIOR':
        return 'error';
      default:
        return 'default';
    }
  };

  const getDifficultyText = (difficulty: string) => {
    switch (difficulty) {
      case 'JUNIOR':
        return '주니어';
      case 'MIDDLE':
        return '미들';
      case 'SENIOR':
        return '시니어';
      default:
        return difficulty;
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(`/resumes/${id}`)}
          sx={{ mr: 2 }}
        >
          이력서로
        </Button>
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          AI 인터뷰 질문
        </Typography>
        <Button
          variant="outlined"
          startIcon={<RefreshIcon />}
          onClick={generateQuestions}
          disabled={loading}
        >
          다시 생성
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              AI가 인터뷰 질문을 생성하고 있습니다...
            </Typography>
            <LinearProgress />
          </CardContent>
        </Card>
      )}

      {questions && (
        <Grid container spacing={3}>
          <Grid sx={{ width: { xs: '100%', md: '66.67%' } }}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                  <Typography variant="h5" sx={{ flexGrow: 1 }}>
                    인터뷰 질문 ({questions.questionCount}개)
                  </Typography>
                  <Chip
                    label={getDifficultyText(questions.difficulty)}
                    color={getDifficultyColor(questions.difficulty) as any}
                  />
                </Box>

                <List>
                  {questions.questions.map((question, index) => (
                    <React.Fragment key={index}>
                      <ListItem alignItems="flex-start">
                        <ListItemText
                          primary={`${index + 1}. ${question}`}
                          primaryTypographyProps={{
                            variant: 'body1',
                            sx: { fontWeight: 500 }
                          }}
                        />
                      </ListItem>
                      {index < questions.questions.length - 1 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              </CardContent>
            </Card>
          </Grid>

          <Grid sx={{ width: { xs: '100%', md: '33.33%' } }}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  AI 분석
                </Typography>
                <Typography variant="body2" paragraph>
                  {questions.analysis}
                </Typography>
                
                <Divider sx={{ my: 2 }} />
                
                <Typography variant="body2" color="text.secondary">
                  난이도 설명
                </Typography>
                <Typography variant="body2" paragraph>
                  {questions.difficultyDescription}
                </Typography>
                
                <Divider sx={{ my: 2 }} />
                
                <Typography variant="body2" color="text.secondary">
                  생성 시간
                </Typography>
                <Typography variant="body2">
                  {new Date(questions.generatedAt).toLocaleString()}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default InterviewQuestions; 