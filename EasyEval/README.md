# *easyEval* - Short Answer Exam Management System

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-FXML-blue?style=for-the-badge&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Apache PDFBox](https://img.shields.io/badge/Apache_PDFBox-PDF_Generation-red?style=for-the-badge&logo=apache&logoColor=white)
![Tesseract OCR](https://img.shields.io/badge/Tesseract-OCR-4B8BBE?style=for-the-badge&logo=google&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-green?style=for-the-badge&logo=architecture&logoColor=white)

## Project Description

*easyEval* is a Java/JavaFX application designed to facilitate the creation and automated evaluation of exams with short answer questions. Teachers can create questions, generate exam documents, and scan completed papers for automatic correction via Optical Character Recognition (OCR).

## Project Architecture

The project adopts the **MVVM** (Model-View-ViewModel) architecture for clear separation of responsibilities and better testability. JavaFX facilitates the implementation of this architecture with its **Property** classes for binding between views and ViewModels.

## Requirements Description

*easyEval* is a Java/JavaFX application aimed at facilitating the creation and automated evaluation of exams with short answer questions. Teachers can create questions, generate exam documents with individual boxes for each letter of the answers, and scan completed papers for automatic correction via Optical Character Recognition (OCR).

### Problems Addressed

- Manual exam correction is time-consuming for teachers
- Commercial solutions are often expensive and rigid
- Difficulty in interpreting handwritten responses in free-form answers

### Main Features

- Generation of standardized documents with individual boxes for each letter of an answer
- Scanning and automatic character recognition via OCR of completed papers
- Automatic correction and results analysis
- Persistent storage of questions, exams, and results

## Functional Testing Plan

| ID | Feature | Test Scenario | Expected Result |
|----|---------|---------------|-----------------|
| T01 | Question creation | Create a question with answer "PARIS" | Question saved in database |
| T02 | Exam generation | Generate document with 10 questions | PDF generated with correct layout and individual answer boxes |
| T03 | Basic OCR | Scan sheet with answer "PARIS" written in boxes | Correct detection of "PARIS" |
| T04 | OCR with rotation | Scan slightly tilted sheet | Automatic tilt correction and correct detection |
| T05 | Score calculation | Scan paper with 7/10 correct answers | Calculated score of 70% and error display |
| T06 | Persistence | Create document, close and reopen application | Document found in available documents list |
| T07 | Multithreading | Launch OCR scan and navigate interface | Responsive interface during OCR processing |

## View Navigation

The easyEval application is structured around four main views:

- **course_manager-view** – Home page displaying available subjects
- **document_manager-view** – Management of exams linked to a specific subject
- **document_creator-view** – Exam editing interface (add/modify questions)
- **submission-view** – Interface for importing and OCR correction of scanned papers

## Technologies Used

- **Java 17+**
- **JavaFX & FXML** - User interface
- **SQLite** - Database
- **Apache PDFBox** - PDF document generation
- **Tesseract OCR** - Optical character recognition (via custom adaptation)

## Task Schedule

### Week 1: Configuration and Architecture
- Class diagram construction ✅ (initial diagram created, improved version in progress)
- Java project configuration with JavaFX ✅
- Git repository setup and project structure ✅
- Repository interfaces creation ✅

### Week 2: Model and Persistence
- Entity implementation (Question, ExamDocument, Submission, ScanResult) ✅
- Repository creation with JDBC ✅
- Database testing ✅

### Week 3: Document Generation + Basic UI
- Exam generation service implementation (ExamDocumentTemplate) ✅
- Document generation testing ✅
- ViewModel implementation (DocumentManagerViewModel, CourseViewModel) ✅
- First user interface ✅

### Week 4, 5 and 6: Multithreading and Optimization
- Centralized navigation system setup between views ✅
- Tesseract OCR configuration ✅
- Character recognition service implementation ✅
- OCR accuracy testing ✅
- Multithreading implementation for OCR (Task, JavaFX Service) ✅
- User interface responsiveness management ✅

### Week 7: Finalization
- General app performance testing ✅
- Bug fixes ✅
- User interface improvements ✅
- Code documentation ✅
- Final testing ✅
- README update: known bugs, differences between initial analysis and implementation ✅
- Project demonstration video creation ✅
- Final project version push ✅

## Differences Between Initial Analysis and Actual Implementation

### OCR Approach: From Individual Box System to Color Detection

**Initial Analysis**:
- Students were supposed to write their answers in individual boxes (one box per letter)
- This approach initially seemed easier for OCR

**Actual Implementation**:
- In practice, box detection proved very problematic
- Difficulties in correctly identifying boxes, question numbers, and student ID
- Adopted solution: using a red pen for answers
- The application extracts only red text from the scanned image
- Answers are identified in order of their appearance on the page

This new approach considerably simplified the OCR process and increased its reliability. By filtering only red writing, we eliminate much of the noise in scanned images.

### Answer Area Organization

**Initial Analysis**:
- Complex structure with individual boxes for each letter
- Specific identification of each question and its associated boxes

**Actual Implementation**:
- Simple division of scanned image into two zones:
  1. Top third: student ID zone (student identifier)
  2. Bottom two-thirds: answer zone
- Answers are associated with questions in order
- Students must write something (even for unknown answers) to maintain alignment

### Current Limitations

- OCR currently only works for one page per student
- Need to have exactly the right number of answers, in the right order
- Tesseract is not optimized for handwriting, requiring VERY CAREFUL writing from students and good quality paper scanning

### Known Bugs

- Incorrect display of creation date when generating a report
- When modifying the last question in the list in the editor (document-creator-view), it overwrites the question above it

## Lessons Learned and Future Developments

Project implementation revealed that certain initial assumptions about the ease of individual box detection were incorrect. Color detection proved to be a simpler and more robust solution.

Possible future improvements:
- Support for multiple pages per exam
- More robust answer area detection
- Interface for manual verification and correction of OCR results
- Support for different question types (multiple choice, long answers, etc.)

## Quick Start

### Prerequisites
- Java 17+
- Tesseract OCR installed on your system
- IntelliJ IDEA (recommended IDE)

### Running the Application

**Recommended approach:**
1. Clone the repository
   ```bash
   git clone https://github.com/retiredRizzler/college-projects/easyEval.git
   ```
2. Open the project in IntelliJ IDEA
3. Let IntelliJ configure the JavaFX dependencies automatically
4. Run the main class from the IDE

**Alternative (Command Line):**
```bash
cd easyeval
./mvnw javafx:run
```
