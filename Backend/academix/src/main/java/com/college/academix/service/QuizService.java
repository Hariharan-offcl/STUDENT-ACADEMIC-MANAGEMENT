package com.college.academix.service;

import com.college.academix.dto.*;
import com.college.academix.exception.BadRequestException;
import com.college.academix.exception.ResourceNotFoundException;
import com.college.academix.model.*;
import com.college.academix.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentQuizAttemptRepository attemptRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // ===================== FACULTY: Create Quiz =====================

    @Transactional
    public Quiz createQuiz(CreateQuizRequest request, User currentUser) {
        Faculty faculty = facultyRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .year(request.getYear())
                .faculty(faculty)
                .department(dept)
                .section(section)
                .subject(subject)
                .questions(new ArrayList<>())
                .build();

        quiz = quizRepository.save(quiz);

        // Create questions
        for (QuestionDTO qDto : request.getQuestions()) {
            Question question = Question.builder()
                    .questionText(qDto.getQuestionText())
                    .optionA(qDto.getOptionA())
                    .optionB(qDto.getOptionB())
                    .optionC(qDto.getOptionC())
                    .optionD(qDto.getOptionD())
                    .correctOption(qDto.getCorrectOption().toUpperCase())
                    .quiz(quiz)
                    .build();
            quiz.getQuestions().add(question);
        }

        return quizRepository.save(quiz);
    }

    // ===================== FACULTY: Get Quizzes =====================

    public List<Quiz> getQuizzesByFaculty(User currentUser) {
        Faculty faculty = facultyRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        return quizRepository.findByFaculty(faculty);
    }

    // ===================== STUDENT: Get Available Quizzes =====================

    public List<Quiz> getQuizzesForStudent(User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return quizRepository.findByDepartmentAndSectionAndYear(
                student.getDepartment(), student.getSection(), student.getCurrentYear());
    }

    // ===================== STUDENT: Attempt Quiz =====================

    @Transactional
    public QuizResultResponse attemptQuiz(Long quizId, SubmitQuizRequest request, User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        // Check if already attempted
        if (attemptRepository.existsByStudentAndQuiz(student, quiz)) {
            throw new BadRequestException("You have already attempted this quiz");
        }

        // Calculate score
        List<Question> questions = questionRepository.findByQuiz(quiz);
        int score = 0;
        int totalQuestions = questions.size();

        for (Question question : questions) {
            String selectedAnswer = request.getAnswers().get(question.getId());
            if (selectedAnswer != null && selectedAnswer.equalsIgnoreCase(question.getCorrectOption())) {
                score++;
            }
        }

        // Save attempt
        StudentQuizAttempt attempt = StudentQuizAttempt.builder()
                .student(student)
                .quiz(quiz)
                .score(score)
                .totalQuestions(totalQuestions)
                .build();
        attemptRepository.save(attempt);

        return QuizResultResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .studentName(currentUser.getName())
                .rollNumber(student.getRollNumber())
                .score(score)
                .totalQuestions(totalQuestions)
                .percentage(totalQuestions > 0 ? (score * 100.0) / totalQuestions : 0)
                .build();
    }

    // ===================== FACULTY: View Quiz Scores =====================

    public List<QuizResultResponse> getQuizScores(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<StudentQuizAttempt> attempts = attemptRepository.findByQuiz(quiz);
        List<QuizResultResponse> results = new ArrayList<>();

        for (StudentQuizAttempt attempt : attempts) {
            results.add(QuizResultResponse.builder()
                    .quizId(quiz.getId())
                    .quizTitle(quiz.getTitle())
                    .studentName(attempt.getStudent().getUser().getName())
                    .rollNumber(attempt.getStudent().getRollNumber())
                    .score(attempt.getScore())
                    .totalQuestions(attempt.getTotalQuestions())
                    .percentage(attempt.getTotalQuestions() > 0
                            ? (attempt.getScore() * 100.0) / attempt.getTotalQuestions()
                            : 0)
                    .build());
        }

        return results;
    }

    // ===================== STUDENT: View Own Quiz Results =====================

    public List<QuizResultResponse> getStudentQuizResults(User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<StudentQuizAttempt> attempts = attemptRepository.findByStudent(student);
        List<QuizResultResponse> results = new ArrayList<>();

        for (StudentQuizAttempt attempt : attempts) {
            results.add(QuizResultResponse.builder()
                    .quizId(attempt.getQuiz().getId())
                    .quizTitle(attempt.getQuiz().getTitle())
                    .studentName(currentUser.getName())
                    .rollNumber(student.getRollNumber())
                    .score(attempt.getScore())
                    .totalQuestions(attempt.getTotalQuestions())
                    .percentage(attempt.getTotalQuestions() > 0
                            ? (attempt.getScore() * 100.0) / attempt.getTotalQuestions()
                            : 0)
                    .build());
        }

        return results;
    }
}
