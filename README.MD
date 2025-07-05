# ATS CV Optimizer

A comprehensive web application that helps users optimize their CVs for Applicant Tracking Systems (ATS) using AI-powered analysis and suggestions.

## Features

- **File Upload Support**: Upload CVs in PDF, DOC, and DOCX formats
- **AI-Powered Analysis**: Uses Grok AI to analyze CV content and structure
- **Interactive Chatbot**: Guided conversation flow for CV optimization
- **Real-time Processing**: WebSocket-based real-time updates
- **ATS Optimization**: Keyword enhancement, formatting improvements, and structure optimization
- **Section Analysis**: Detailed analysis of CV sections (contact info, experience, education, skills, etc.)
- **Missing Information Detection**: Identifies and prompts for missing critical information
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

- **Backend**: Spring Boot 3.5.0, Java 21
- **Database**: MySQL 8.4.5
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **WebSocket**: STOMP protocol for real-time communication
- **File Processing**: Apache POI (DOC/DOCX), PDFBox (PDF)
- **AI Integration**: Grok AI API
- **Build Tool**: Maven

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker (for MySQL database)
- Grok AI API key

## Setup Instructions

### 1. Database Setup

The application uses MySQL running in a Docker container. Make sure you have the MySQL container running:

```bash
# Check if MySQL container is running
docker ps

# If not running, start it (assuming container name is 'mysql-db')
docker start mysql-db

# Verify the 'ats' database exists
docker exec -it mysql-db mysql -u root -proot -e "SHOW DATABASES;"
```

### 2. Environment Configuration

Set up your Grok AI API key as an environment variable:

```bash
export GROK_API_KEY=your-actual-grok-api-key-here
```

Or update the `application.properties` file:

```properties
app.ai.grok.api-key=your-actual-grok-api-key-here
```

### 3. Build and Run

```bash
# Clone the repository (if not already done)
cd /var/www/html/tekup/ats

# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Verify Setup

1. Open your browser and navigate to `http://localhost:8080`
2. You should see the ATS CV Optimizer homepage
3. Click "Start Optimizing" to access the chat interface
4. The database tables should be automatically created

## Usage Guide

### 1. Access the Application

- **Homepage**: `http://localhost:8080` - Overview and features
- **Chat Interface**: `http://localhost:8080/chat` - Main optimization interface

### 2. CV Optimization Process

1. **Start Chat**: Navigate to the chat interface
2. **Upload CV**: Use the file upload area to upload your CV (PDF, DOC, or DOCX)
3. **AI Analysis**: The system will automatically extract text and analyze your CV
4. **Review Sections**: The AI will identify different CV sections and their completeness
5. **Get Suggestions**: Receive specific recommendations for improvement
6. **Optimization**: The system will provide optimized versions of your CV sections
7. **Download**: Get your improved, ATS-friendly CV

### 3. Supported File Formats

- **PDF**: `.pdf` files
- **Microsoft Word**: `.doc` and `.docx` files
- **File Size Limit**: Maximum 10MB per file

### 4. Features Overview

#### Chat Interface
- Real-time conversation with AI assistant
- File upload with drag-and-drop support
- Progress indicators for file processing
- Chat history preservation

#### CV Analysis
- **Contact Information**: Email, phone, LinkedIn profile validation
- **Professional Summary**: Content quality and completeness check
- **Work Experience**: Job titles, companies, dates, descriptions analysis
- **Education**: Degree, institution, graduation year verification
- **Skills**: Technical and soft skills identification
- **Additional Sections**: Certifications, projects, languages, achievements

#### Optimization Features
- **Keyword Enhancement**: Industry-specific keyword suggestions
- **Format Standardization**: ATS-friendly formatting
- **Structure Improvement**: Proper section organization
- **Achievement Quantification**: Adding metrics and numbers
- **Missing Information Detection**: Prompts for incomplete sections

## API Endpoints

### File Upload
- `POST /api/cv/upload` - Upload CV file
- `GET /api/cv/list` - Get user's uploaded CVs
- `GET /api/cv/{cvId}/status` - Check CV processing status
- `DELETE /api/cv/{cvId}` - Delete uploaded CV

### Optimization
- `GET /api/optimization/{cvId}/sections` - Get CV sections
- `GET /api/optimization/{cvId}/results` - Get optimization results
- `POST /api/optimization/{cvId}/optimize` - Request optimization

### Chat
- `GET /api/chat/history` - Get chat message history
- WebSocket endpoints for real-time messaging

## Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ats
spring.datasource.username=root
spring.datasource.password=root
```

### File Upload Configuration
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
app.file.upload-dir=./uploads
app.file.allowed-extensions=pdf,doc,docx
```

### AI Configuration
```properties
app.ai.grok.api-key=${GROK_API_KEY:your-grok-api-key-here}
app.ai.grok.base-url=https://api.x.ai/v1
app.ai.grok.model=grok-beta
```

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL container is running
   - Check database credentials in `application.properties`
   - Verify the 'ats' database exists

2. **File Upload Issues**
   - Check file size (max 10MB)
   - Ensure file format is supported (PDF, DOC, DOCX)
   - Verify upload directory permissions

3. **AI API Errors**
   - Verify Grok API key is set correctly
   - Check API rate limits
   - Ensure internet connectivity

4. **WebSocket Connection Issues**
   - Check browser console for errors
   - Verify WebSocket endpoint configuration
   - Clear browser cache and cookies

### Logs

Application logs are available in the console output. For debugging:

```bash
# Run with debug logging
mvn spring-boot:run -Dspring.profiles.active=debug
```

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/tekup/ats/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST and web controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   └── service/        # Business logic services
│   └── resources/
│       ├── static/         # CSS, JS, images
│       ├── templates/      # Thymeleaf templates
│       └── application.properties
```

### Adding New Features

1. **New CV Section Types**: Add to `CvSection.SectionType` enum
2. **Additional File Formats**: Extend `FileProcessingService`
3. **New AI Prompts**: Modify `AiService` methods
4. **UI Enhancements**: Update templates and static files

## Security Considerations

- File upload validation and sanitization
- Session-based user management
- Input validation for all user inputs
- Secure file storage with unique filenames
- API rate limiting (recommended for production)

## Performance Optimization

- Asynchronous file processing
- Database connection pooling
- File cleanup for old uploads
- Caching for frequently accessed data

## License

This project is developed for educational and demonstration purposes.

## Support

For issues and questions, please check the troubleshooting section or review the application logs for detailed error information.