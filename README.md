# Key Account Manager (KAM) Lead Management System

## Introduction
Udaan, a B2B e-commerce platform, requires a Lead Management System for Key Account Managers (KAMs) who manage relationships with large restaurant accounts.  
This system helps track leads, manage interactions, plan calls, and analyze account performance efficiently.

---

## Requirements

### Core Requirements

#### 1. Lead Management
- Add new restaurant leads  
- Store basic restaurant information  
- Track lead status (e.g., New, Active, Converted, Inactive)

#### 2. Contact Management
- Support multiple Points of Contact (POCs) per restaurant  
- Store contact details:
  - Name  
  - Role  
  - Phone / Email  
- Allow multiple POCs with different roles per restaurant

#### 3. Interaction Tracking
- Record all calls made to leads  
- Track orders placed by restaurants  
- Store interaction dates and detailed notes

#### 4. Call Planning
- Set call frequency for each lead  
- Display leads requiring calls **today**  
- Track the last call made for each lead

#### 5. Performance Tracking
- Identify well-performing accounts  
- Monitor ordering patterns and order frequency  
- Detect underperforming or inactive accounts

---

## Technical Requirements

### Data Models
- Design database schema / data structures  
- Manage entity relationships effectively  
- Enable efficient querying and filtering

### API Design
- RESTful APIs for all operations  
- Proper error handling  
- Authentication and authorization support

### Business Logic
- Determine which leads require calls today  
- Calculate account performance metrics  
- Handle lead status transitions correctly

---

## Implementation Guide

### System Design Questions
- How would you structure this application at a high level?  
- Would SQL or NoSQL be a better choice and why?  
- How would you scale this system as the number of users grows?

### Data Modeling Questions
- What core tables / collections are required?  
- How are entities related to each other?  
- Which fields should be indexed for performance?

---

## Implementation Details
- Implement all requirements listed above  
- Follow modular and clean architecture principles  

---

## Edge Cases
- Handling KAM reassignment for existing leads  
- Managing timezone differences in call scheduling  
- Handling missed calls or delayed interactions

---

## Submission Guidelines

### Code Requirements
- Fully working application  
- Clear dependency specification  
- Sample data included for testing/demo

### Documentation Requirements
`README.md` must include:
- Project overview  
- System requirements  
- Installation instructions  
- Running instructions  
- Test execution guide  
- API documentation  
- Sample usage examples  

---

## Video Demonstration
Provide a **5–10 minute video** demonstrating:
- Code setup process  
- Application running  
- Major features  
- Sample inputs and outputs  

**Format:** MP4

---

## Submission Format
Submit a ZIP file containing:
- Source code directory  
- `README.md`  
- `requirements.txt` / `package.json`  
- Demonstration video (`demo.mp4`)

---

## Evaluation Criteria

### Modularity
- Separation of concerns  
- Use of design patterns  
- Reusable components  
- Well-defined interfaces  

### Extensibility
- Ease of adding new features  
- Configurable components  
- Use of interfaces / abstractions  

### Completeness
- All required features implemented  
- Proper error handling  
- Edge case coverage  
- Comprehensive testing  

### Code Readability
- Clear naming conventions  
- Consistent coding style  
- Meaningful comments  
- Good documentation  

---

## Bonus Features

### Unit Testing
- High test coverage  
- Edge case testing  
- Integration tests  
- Test documentation  

### REST API Implementation
- RESTful endpoint design  
- API documentation  
- Proper HTTP status codes  
- Request/response validation  

### Authentication & Authorization
- Secure access control  
- Role-based permissions  

### Functional User Interface
- Clean and intuitive UI  
- Responsive design  
- Interactive features  
- Error handling  
- Cross-browser compatibility  

### Deployment
- Online deployment  
- Deployment documentation  
- Environment configuration  
- CI/CD pipeline setup  

---

## Project Timeline
- **Submission Deadline:** January 5th, 2025  
- Early submissions encouraged  
- Rolling evaluation process  

---

## Submission Process
1. Complete implementation  
2. Record demo video  
3. Create ZIP file with all components  
4. Verify requirements  
5. Submit via email  

---

## Important Notes
- Incomplete submissions will not be considered  
- Original work is mandatory  
- Third-party libraries are allowed with justification  
- Plagiarism will result in disqualification  
