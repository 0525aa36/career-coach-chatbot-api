import React, { useState, useEffect } from 'react';
import {
  Container,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  SelectChangeEvent,
  Alert,
  CircularProgress,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import { useNavigate, useParams } from 'react-router-dom';
import { CreateResumeRequest, JobRole } from '../types';
import { resumeApi } from '../services/api';

const ResumeForm: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const isEdit = Boolean(id);

  const [formData, setFormData] = useState<CreateResumeRequest>({
    careerSummary: '',
    jobRole: JobRole.BACKEND_DEVELOPER,
    experienceYears: 0,
    projectExperience: '',
    techSkills: [],
  });

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string>('');
  const [newTechSkill, setNewTechSkill] = useState('');

  useEffect(() => {
    if (isEdit && id) {
      loadResume(parseInt(id));
    }
  }, [isEdit, id]);

  const loadResume = async (resumeId: number) => {
    setLoading(true);
    try {
      const resume = await resumeApi.getResume(resumeId);
      setFormData({
        careerSummary: resume.careerSummary,
        jobRole: resume.jobRole,
        experienceYears: resume.experienceYears,
        projectExperience: resume.projectExperience || '',
        techSkills: resume.techSkills || [],
      });
    } catch (error) {
      setError('이력서를 불러오는데 실패했습니다.');
      console.error('이력서 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (field: keyof CreateResumeRequest, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleJobRoleChange = (event: SelectChangeEvent<JobRole>) => {
    handleInputChange('jobRole', event.target.value as JobRole);
  };

  const handleTechSkillAdd = () => {
    if (newTechSkill.trim() && !formData.techSkills?.includes(newTechSkill.trim())) {
      handleInputChange('techSkills', [...(formData.techSkills || []), newTechSkill.trim()]);
      setNewTechSkill('');
    }
  };

  const handleTechSkillDelete = (skillToDelete: string) => {
    handleInputChange('techSkills', formData.techSkills?.filter(skill => skill !== skillToDelete) || []);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    try {
      if (isEdit && id) {
        await resumeApi.updateResume(parseInt(id), formData);
      } else {
        await resumeApi.createResume(formData);
      }
      navigate('/resumes');
    } catch (error: any) {
      setError(error.response?.data?.message || '저장에 실패했습니다.');
      console.error('이력서 저장 실패:', error);
    } finally {
      setSaving(false);
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

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            {isEdit ? '이력서 수정' : '새 이력서 작성'}
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <Grid container spacing={3}>
              <Grid sx={{ width: '100%' }}>
                <TextField
                  fullWidth
                  label="경력 요약"
                  value={formData.careerSummary}
                  onChange={(e) => handleInputChange('careerSummary', e.target.value)}
                  required
                  multiline
                  rows={3}
                  helperText="주요 경력과 전문 분야를 간단히 요약해주세요"
                />
              </Grid>

              <Grid sx={{ width: { xs: '100%', sm: '50%' } }}>
                <FormControl fullWidth required>
                  <InputLabel>직무 역할</InputLabel>
                  <Select
                    value={formData.jobRole}
                    label="직무 역할"
                    onChange={handleJobRoleChange}
                  >
                    {Object.values(JobRole).map((role) => (
                      <MenuItem key={role} value={role}>
                        {getJobRoleDisplayName(role)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid sx={{ width: { xs: '100%', sm: '50%' } }}>
                <TextField
                  fullWidth
                  label="경력 연수"
                  type="number"
                  value={formData.experienceYears}
                  onChange={(e) => handleInputChange('experienceYears', parseInt(e.target.value) || 0)}
                  required
                  inputProps={{ min: 0, max: 50 }}
                  helperText="0-50년 범위로 입력해주세요"
                />
              </Grid>

              <Grid sx={{ width: '100%' }}>
                <TextField
                  fullWidth
                  label="프로젝트 경험"
                  value={formData.projectExperience}
                  onChange={(e) => handleInputChange('projectExperience', e.target.value)}
                  multiline
                  rows={4}
                  helperText="주요 프로젝트 경험을 상세히 기술해주세요"
                />
              </Grid>

              <Grid sx={{ width: '100%' }}>
                <Typography variant="subtitle1" gutterBottom>
                  기술 스택
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                  <TextField
                    label="기술 스택 추가"
                    value={newTechSkill}
                    onChange={(e) => setNewTechSkill(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleTechSkillAdd())}
                    size="small"
                    sx={{ flexGrow: 1 }}
                  />
                  <Button
                    variant="outlined"
                    onClick={handleTechSkillAdd}
                    disabled={!newTechSkill.trim()}
                  >
                    추가
                  </Button>
                </Box>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  {formData.techSkills?.map((skill) => (
                    <Chip
                      key={skill}
                      label={skill}
                      onDelete={() => handleTechSkillDelete(skill)}
                      color="primary"
                      variant="outlined"
                    />
                  ))}
                </Box>
              </Grid>

              <Grid sx={{ width: '100%' }}>
                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                  <Button
                    variant="outlined"
                    onClick={() => navigate('/resumes')}
                    disabled={saving}
                  >
                    취소
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={saving}
                    startIcon={saving ? <CircularProgress size={20} /> : null}
                  >
                    {saving ? '저장 중...' : (isEdit ? '수정' : '저장')}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default ResumeForm; 