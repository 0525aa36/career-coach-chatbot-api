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
  Divider,
  Alert,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {
  ArrowBack as ArrowBackIcon,
  Refresh as RefreshIcon,
  ExpandMore as ExpandMoreIcon,
  AccessTime as TimeIcon,
  Book as BookIcon,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import { LearningPathResponse } from '../types';
import { learningPathApi } from '../services/api';

const LearningPath: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [learningPath, setLearningPath] = useState<LearningPathResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const generateLearningPath = useCallback(async () => {
    if (!id) return;
    
    setLoading(true);
    setError('');
    
    try {
      const data = await learningPathApi.generateLearningPath(parseInt(id));
      setLearningPath(data);
    } catch (error) {
      setError('학습 경로 생성에 실패했습니다.');
      console.error('학습 경로 생성 실패:', error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (id) {
      generateLearningPath();
    }
  }, [id, generateLearningPath]);

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'BEGINNER':
        return 'success';
      case 'INTERMEDIATE':
        return 'warning';
      case 'ADVANCED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getDifficultyText = (difficulty: string) => {
    switch (difficulty) {
      case 'BEGINNER':
        return '초급';
      case 'INTERMEDIATE':
        return '중급';
      case 'ADVANCED':
        return '고급';
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
          AI 학습 경로 추천
        </Typography>
        <Button
          variant="outlined"
          startIcon={<RefreshIcon />}
          onClick={generateLearningPath}
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
              AI가 맞춤형 학습 경로를 생성하고 있습니다...
            </Typography>
            <LinearProgress />
          </CardContent>
        </Card>
      )}

      {learningPath && (
        <Grid container spacing={3}>
          <Grid sx={{ width: { xs: '100%', md: '66.67%' } }}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                  <Typography variant="h5" sx={{ flexGrow: 1 }}>
                    학습 단계 ({learningPath.totalSteps}단계)
                  </Typography>
                  <Chip
                    label={learningPath.experienceLevel}
                    color="primary"
                    variant="outlined"
                  />
                </Box>

                <Typography variant="body1" paragraph sx={{ mb: 3 }}>
                  {learningPath.overallStrategy}
                </Typography>

                <List>
                  {learningPath.learningSteps.map((step, index) => (
                    <Accordion key={index} sx={{ mb: 2 }}>
                      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                        <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                          <Box sx={{ 
                            width: 32, 
                            height: 32, 
                            borderRadius: '50%', 
                            bgcolor: 'primary.main', 
                            color: 'white',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            mr: 2,
                            fontSize: '0.875rem',
                            fontWeight: 'bold'
                          }}>
                            {index + 1}
                          </Box>
                          <Box sx={{ flexGrow: 1 }}>
                            <Typography variant="h6">
                              {step.title}
                            </Typography>
                            <Box sx={{ display: 'flex', gap: 1, mt: 0.5 }}>
                              <Chip
                                label={getDifficultyText(step.difficulty)}
                                color={getDifficultyColor(step.difficulty) as any}
                                size="small"
                              />
                              <Chip
                                icon={<TimeIcon />}
                                label={step.estimatedTime}
                                size="small"
                                variant="outlined"
                              />
                            </Box>
                          </Box>
                        </Box>
                      </AccordionSummary>
                      <AccordionDetails>
                        <Typography variant="body2" paragraph>
                          {step.description}
                        </Typography>
                        
                        <Typography variant="subtitle2" gutterBottom>
                          학습 목표
                        </Typography>
                        <Typography variant="body2" paragraph>
                          {step.learningObjective}
                        </Typography>
                        
                        <Typography variant="subtitle2" gutterBottom>
                          추천 자료
                        </Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                          {step.resources.map((resource, resourceIndex) => (
                            <Chip
                              key={resourceIndex}
                              label={resource}
                              icon={<BookIcon />}
                              size="small"
                              variant="outlined"
                            />
                          ))}
                        </Box>
                      </AccordionDetails>
                    </Accordion>
                  ))}
                </List>
              </CardContent>
            </Card>
          </Grid>

          <Grid sx={{ width: { xs: '100%', md: '33.33%' } }}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  학습 정보
                </Typography>
                
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    직무 역할
                  </Typography>
                  <Typography variant="body1" paragraph>
                    {learningPath.jobRole}
                  </Typography>
                </Box>

                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    경험 레벨
                  </Typography>
                  <Typography variant="body1" paragraph>
                    {learningPath.experienceLevel}
                  </Typography>
                </Box>

                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    예상 소요 시간
                  </Typography>
                  <Typography variant="body1" paragraph>
                    {learningPath.estimatedDuration}
                  </Typography>
                </Box>
                
                <Divider sx={{ my: 2 }} />
                
                <Typography variant="body2" color="text.secondary">
                  생성 시간
                </Typography>
                <Typography variant="body2">
                  {new Date(learningPath.generatedAt).toLocaleString()}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default LearningPath; 