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
  Divider,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  QuestionAnswer as InterviewIcon,
  School as SchoolIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import { ResumeDto, JobRole } from '../types';
import { resumeApi } from '../services/api';

const ResumeDetail: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [resume, setResume] = useState<ResumeDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      loadResume(parseInt(id));
    }
  }, [id]);

  const loadResume = async (resumeId: number) => {
    try {
      const data = await resumeApi.getResume(resumeId);
      setResume(data);
    } catch (error) {
      console.error('이력서 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!resume) return;
    
    if (window.confirm('정말로 이 이력서를 삭제하시겠습니까?')) {
      try {
        await resumeApi.deleteResume(resume.id);
        navigate('/resumes');
      } catch (error) {
        console.error('이력서 삭제 실패:', error);
      }
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

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <LinearProgress />
      </Container>
    );
  }

  if (!resume) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Typography variant="h6" color="error">
          이력서를 찾을 수 없습니다.
        </Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/resumes')}
          sx={{ mr: 2 }}
        >
          목록으로
        </Button>
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          이력서 상세
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<InterviewIcon />}
            onClick={() => navigate(`/resumes/${resume.id}/interview`)}
          >
            인터뷰 질문
          </Button>
          <Button
            variant="outlined"
            startIcon={<SchoolIcon />}
            onClick={() => navigate(`/resumes/${resume.id}/learning-path`)}
          >
            학습 경로
          </Button>
          <Button
            variant="outlined"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/resumes/${resume.id}/edit`)}
          >
            수정
          </Button>
          <Button
            variant="outlined"
            color="error"
            startIcon={<DeleteIcon />}
            onClick={handleDelete}
          >
            삭제
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3}>
        <Grid sx={{ width: { xs: '100%', md: '66.67%' } }}>
          <Card>
            <CardContent>
              <Typography variant="h5" gutterBottom>
                {resume.careerSummary}
              </Typography>
              
              <Box sx={{ display: 'flex', gap: 1, mb: 3 }}>
                <Chip
                  label={getJobRoleDisplayName(resume.jobRole)}
                  color="primary"
                  variant="outlined"
                />
                <Chip
                  label={`${resume.experienceYears}년 경력`}
                  color="primary"
                  variant="outlined"
                />
                <Chip
                  label={getDifficultyText(resume.interviewDifficulty)}
                  color={getDifficultyColor(resume.interviewDifficulty) as any}
                />
              </Box>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6" gutterBottom>
                프로젝트 경험
              </Typography>
              <Typography variant="body1" paragraph>
                {resume.projectExperience || '프로젝트 경험이 없습니다.'}
              </Typography>

              {resume.techSkills && resume.techSkills.length > 0 && (
                <>
                  <Divider sx={{ my: 2 }} />
                  <Typography variant="h6" gutterBottom>
                    기술 스택
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {resume.techSkills.map((skill) => (
                      <Chip
                        key={skill}
                        label={skill}
                        color="primary"
                        variant="outlined"
                      />
                    ))}
                  </Box>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid sx={{ width: { xs: '100%', md: '33.33%' } }}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                이력서 정보
              </Typography>
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  작성일
                </Typography>
                <Typography variant="body1">
                  {new Date(resume.createdAt).toLocaleDateString()}
                </Typography>
              </Box>

              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  경험 레벨
                </Typography>
                <Typography variant="body1">
                  {resume.experienceLevel}
                </Typography>
              </Box>

              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  인터뷰 난이도
                </Typography>
                <Chip
                  label={getDifficultyText(resume.interviewDifficulty)}
                  color={getDifficultyColor(resume.interviewDifficulty) as any}
                  size="small"
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ResumeDetail; 