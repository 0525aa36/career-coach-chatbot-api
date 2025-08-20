import React, { useState, useEffect } from 'react';
import {
  Container,
  Card,
  CardContent,
  Typography,
  Button,
  Box,
  Chip,
  LinearProgress,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {
  Work as WorkIcon,
  School as SchoolIcon,
  QuestionAnswer as InterviewIcon,
  TrendingUp as TrendingUpIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { ResumeDto, JobRole } from '../types';
import { resumeApi } from '../services/api';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [resumes, setResumes] = useState<ResumeDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadResumes();
  }, []);

  const loadResumes = async () => {
    try {
      const data = await resumeApi.getAllResumes();
      setResumes(data);
    } catch (error) {
      console.error('이력서 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const getJobRoleDisplayName = (jobRole: JobRole): string => {
    const displayNames: Record<JobRole, string> = {
      [JobRole.BACKEND_DEVELOPER]: '백엔드 개발자',
      [JobRole.FRONTEND_DEVELOPER]: '프론트엔드 개발자',
      [JobRole.FULLSTACK_DEVELOPER]: '풀스택 개발자',
      [JobRole.DEVOPS_ENGINEER]: 'DevOps 엔지니어',
      [JobRole.DATA_SCIENTIST]: '데이터 사이언티스트',
      [JobRole.DATA_ENGINEER]: '데이터 엔지니어',
      [JobRole.ML_ENGINEER]: 'ML 엔지니어',
      [JobRole.AI_ENGINEER]: 'AI 엔지니어',
      [JobRole.PRODUCT_MANAGER]: '프로덕트 매니저',
      [JobRole.QA_ENGINEER]: 'QA 엔지니어',
      [JobRole.SECURITY_ENGINEER]: '보안 엔지니어',
      [JobRole.SYSTEM_ARCHITECT]: '시스템 아키텍트',
    };
    return displayNames[jobRole] || jobRole;
  };

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

  const recentResumes = resumes.slice(0, 5);

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <LinearProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        커리어 코치 대시보드
      </Typography>
      <Typography variant="subtitle1" color="text.secondary" gutterBottom>
        이력서 기반 개인 맞춤형 커리어 관리 시스템
      </Typography>

      {/* 통계 카드 */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid sx={{ width: { xs: '100%', sm: '50%', md: '25%' } }}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <WorkIcon sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                <Box>
                  <Typography variant="h4">{resumes.length}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    등록된 이력서
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid sx={{ width: { xs: '100%', sm: '50%', md: '25%' } }}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <InterviewIcon sx={{ fontSize: 40, color: 'secondary.main', mr: 2 }} />
                <Box>
                  <Typography variant="h4">AI</Typography>
                  <Typography variant="body2" color="text.secondary">
                    인터뷰 질문 생성
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid sx={{ width: { xs: '100%', sm: '50%', md: '25%' } }}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <SchoolIcon sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                <Box>
                  <Typography variant="h4">AI</Typography>
                  <Typography variant="body2" color="text.secondary">
                    학습 경로 추천
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid sx={{ width: { xs: '100%', sm: '50%', md: '25%' } }}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <TrendingUpIcon sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                <Box>
                  <Typography variant="h4">실시간</Typography>
                  <Typography variant="body2" color="text.secondary">
                    성장 분석
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* 빠른 액션 */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid sx={{ width: '100%' }}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                빠른 액션
              </Typography>
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => navigate('/resumes/new')}
                >
                  새 이력서 작성
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<InterviewIcon />}
                  onClick={() => navigate('/resumes')}
                >
                  인터뷰 질문 생성
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<SchoolIcon />}
                  onClick={() => navigate('/resumes')}
                >
                  학습 경로 추천
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* 최근 이력서 */}
      <Grid container spacing={3}>
        <Grid sx={{ width: '100%' }}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                최근 등록된 이력서
              </Typography>
              {recentResumes.length === 0 ? (
                <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
                  등록된 이력서가 없습니다. 첫 번째 이력서를 작성해보세요!
                </Typography>
              ) : (
                <Grid container spacing={2}>
                  {recentResumes.map((resume) => (
                    <Grid sx={{ width: { xs: '100%', sm: '50%', md: '33.33%' } }} key={resume.id}>
                      <Card variant="outlined" sx={{ cursor: 'pointer' }} onClick={() => navigate(`/resumes/${resume.id}`)}>
                        <CardContent>
                          <Typography variant="h6" noWrap>
                            {resume.careerSummary}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" gutterBottom>
                            {getJobRoleDisplayName(resume.jobRole)}
                          </Typography>
                          <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                            <Chip
                              label={`${resume.experienceYears}년 경력`}
                              size="small"
                              color="primary"
                              variant="outlined"
                            />
                            <Chip
                              label={getDifficultyText(resume.interviewDifficulty)}
                              size="small"
                              color={getDifficultyColor(resume.interviewDifficulty) as any}
                            />
                          </Box>
                          <Typography variant="caption" color="text.secondary">
                            {new Date(resume.createdAt).toLocaleDateString()}
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Dashboard; 