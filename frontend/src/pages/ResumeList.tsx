import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  Card,
  CardContent,
  Chip,
  IconButton,
  LinearProgress,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { ResumeDto, JobRole } from '../types';
import { resumeApi } from '../services/api';

const ResumeList: React.FC = () => {
  const navigate = useNavigate();
  const [resumes, setResumes] = useState<ResumeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [jobRoleFilter, setJobRoleFilter] = useState<string>('');

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

  const handleDelete = async (id: number) => {
    if (window.confirm('정말로 이 이력서를 삭제하시겠습니까?')) {
      try {
        await resumeApi.deleteResume(id);
        setResumes(resumes.filter(resume => resume.id !== id));
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

  const filteredResumes = resumes.filter(resume => {
    const matchesSearch = resume.careerSummary.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         resume.projectExperience?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         resume.techSkills?.some(skill => skill.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesJobRole = !jobRoleFilter || resume.jobRole === jobRoleFilter;
    return matchesSearch && matchesJobRole;
  });

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <LinearProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          이력서 관리
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/resumes/new')}
        >
          새 이력서 작성
        </Button>
      </Box>

      {/* 필터 */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid sx={{ width: { xs: '100%', sm: '50%' } }}>
              <TextField
                fullWidth
                label="검색"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                }}
                placeholder="경력 요약, 프로젝트 경험, 기술 스택으로 검색"
              />
            </Grid>
            <Grid sx={{ width: { xs: '100%', sm: '50%' } }}>
              <FormControl fullWidth>
                <InputLabel>직무 역할 필터</InputLabel>
                <Select
                  value={jobRoleFilter}
                  label="직무 역할 필터"
                  onChange={(e) => setJobRoleFilter(e.target.value)}
                >
                  <MenuItem value="">전체</MenuItem>
                  {Object.values(JobRole).map((role) => (
                    <MenuItem key={role} value={role}>
                      {getJobRoleDisplayName(role)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* 이력서 목록 */}
      {filteredResumes.length === 0 ? (
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 4 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {resumes.length === 0 ? '등록된 이력서가 없습니다.' : '검색 결과가 없습니다.'}
            </Typography>
            {resumes.length === 0 && (
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => navigate('/resumes/new')}
                sx={{ mt: 2 }}
              >
                첫 번째 이력서 작성하기
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {filteredResumes.map((resume) => (
            <Grid sx={{ width: { xs: '100%', sm: '50%', md: '33.33%' } }} key={resume.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" gutterBottom noWrap>
                    {resume.careerSummary}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {getJobRoleDisplayName(resume.jobRole)}
                  </Typography>
                  
                  <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
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

                  {resume.techSkills && resume.techSkills.length > 0 && (
                    <Box sx={{ mb: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        기술 스택:
                      </Typography>
                      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mt: 0.5 }}>
                        {resume.techSkills.slice(0, 3).map((skill) => (
                          <Chip
                            key={skill}
                            label={skill}
                            size="small"
                            variant="outlined"
                          />
                        ))}
                        {resume.techSkills.length > 3 && (
                          <Chip
                            label={`+${resume.techSkills.length - 3}`}
                            size="small"
                            variant="outlined"
                          />
                        )}
                      </Box>
                    </Box>
                  )}

                  <Typography variant="caption" color="text.secondary">
                    {new Date(resume.createdAt).toLocaleDateString()}
                  </Typography>
                </CardContent>

                <Box sx={{ p: 2, pt: 0 }}>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <IconButton
                      size="small"
                      onClick={() => navigate(`/resumes/${resume.id}`)}
                      color="primary"
                    >
                      <ViewIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => navigate(`/resumes/${resume.id}/edit`)}
                      color="primary"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDelete(resume.id)}
                      color="error"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </Box>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default ResumeList; 