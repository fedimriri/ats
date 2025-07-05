-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Jul 04, 2025 at 01:09 PM
-- Server version: 8.4.5
-- PHP Version: 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ats`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat_messages`
--

CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `message` text NOT NULL,
  `message_type` varchar(255) DEFAULT NULL,
  `metadata` text,
  `sender_type` enum('BOT','USER') NOT NULL,
  `chat_session_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `chat_messages`
--

INSERT INTO `chat_messages` (`id`, `created_at`, `message`, `message_type`, `metadata`, `sender_type`, `chat_session_id`) VALUES
(1, '2025-07-04 13:02:19.816394', 'Welcome to the ATS CV Optimizer! I\'m here to help you create an ATS-friendly CV. Please upload your CV file (PDF, DOC, or DOCX) to get started.', 'welcome', NULL, 'BOT', 1),
(2, '2025-07-04 13:05:20.860436', 'Welcome to the ATS CV Optimizer! I\'m here to help you create an ATS-friendly CV. Please upload your CV file (PDF, DOC, or DOCX) to get started.', 'welcome', NULL, 'BOT', 2),
(3, '2025-07-04 13:05:30.178863', 'I\'ve uploaded my CV: safe_resume.pdf', 'text', NULL, 'USER', 2),
(4, '2025-07-04 13:05:30.183542', 'Great! Please use the file upload button below to upload your CV. I accept PDF, DOC, and DOCX files up to 10MB.', 'file_prompt', NULL, 'BOT', 2),
(5, '2025-07-04 13:07:38.248692', 'I\'ve uploaded my CV: safe_resume.pdf', 'text', NULL, 'USER', 2),
(6, '2025-07-04 13:07:38.253798', 'Great! Please use the file upload button below to upload your CV. I accept PDF, DOC, and DOCX files up to 10MB.', 'file_prompt', NULL, 'BOT', 2);

-- --------------------------------------------------------

--
-- Table structure for table `chat_sessions`
--

CREATE TABLE `chat_sessions` (
  `id` bigint NOT NULL,
  `context_data` text,
  `created_at` datetime(6) DEFAULT NULL,
  `current_step` varchar(255) DEFAULT NULL,
  `session_id` varchar(255) NOT NULL,
  `status` enum('ABANDONED','ACTIVE','COMPLETED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `chat_sessions`
--

INSERT INTO `chat_sessions` (`id`, `context_data`, `created_at`, `current_step`, `session_id`, `status`, `updated_at`, `user_id`) VALUES
(1, NULL, '2025-07-04 13:02:19.809418', 'welcome', '249819d0-f439-441e-8203-8d5558e5a40c', 'ACTIVE', '2025-07-04 13:02:19.809466', 1),
(2, NULL, '2025-07-04 13:05:20.854501', 'welcome', '11b42360-6f9f-453e-a250-9cab7f8babc8', 'ACTIVE', '2025-07-04 13:05:20.854537', 2);

-- --------------------------------------------------------

--
-- Table structure for table `cv_sections`
--

CREATE TABLE `cv_sections` (
  `id` bigint NOT NULL,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `is_complete` bit(1) DEFAULT NULL,
  `missing_fields` text,
  `original_content` text,
  `section_order` int DEFAULT NULL,
  `section_title` varchar(255) DEFAULT NULL,
  `section_type` enum('ACHIEVEMENTS','CERTIFICATIONS','CONTACT_INFO','EDUCATION','LANGUAGES','OTHER','PROFESSIONAL_SUMMARY','PROJECTS','REFERENCES','SKILLS','WORK_EXPERIENCE') NOT NULL,
  `cv_upload_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `cv_sections`
--

INSERT INTO `cv_sections` (`id`, `content`, `created_at`, `is_complete`, `missing_fields`, `original_content`, `section_order`, `section_title`, `section_type`, `cv_upload_id`) VALUES
(1, 'Content optimized for ATS compatibility with improved formatting and relevant keywords.', '2025-07-04 13:05:32.481169', b'1', 'Phone number, LinkedIn profile', 'Email: safsoltani311998@gmail.com\n', 1, 'Contact Information', 'CONTACT_INFO', 1),
(2, '', '2025-07-04 13:05:32.484885', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 2, 'Professional Summary', 'PROFESSIONAL_SUMMARY', 1),
(3, '', '2025-07-04 13:05:32.488035', b'0', 'Consider adding: Specific dates (MM/YYYY), Company names, Job titles, Quantified achievements', '', 3, 'Work Experience', 'WORK_EXPERIENCE', 1),
(4, '', '2025-07-04 13:05:32.490805', b'0', 'Consider adding: Degree type, Institution name, Graduation date, GPA (if 3.5+)', '', 4, 'Education', 'EDUCATION', 1),
(5, 'Technical Skills: Programming, Data Analysis, Project Management\nSoft Skills: Leadership, Communication, Problem Solving, Team Collaboration\nTools & Technologies: Microsoft Office, Project Management Software\n', '2025-07-04 13:05:32.493542', b'1', 'Technical skills, soft skills, programming languages, tools', 'Médicales de Tunis', 5, 'Skills', 'SKILLS', 1),
(6, '', '2025-07-04 13:05:32.496433', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 6, 'Certifications', 'CERTIFICATIONS', 1),
(7, '', '2025-07-04 13:05:32.500328', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 7, 'Projects', 'PROJECTS', 1),
(8, '', '2025-07-04 13:05:32.503037', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 8, 'Languages', 'LANGUAGES', 1),
(9, 'Content optimized for ATS compatibility with improved formatting and relevant keywords.', '2025-07-04 13:07:40.608470', b'1', 'Phone number, LinkedIn profile', 'Email: safsoltani311998@gmail.com\n', 1, 'Contact Information', 'CONTACT_INFO', 2),
(10, '', '2025-07-04 13:07:40.613185', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 2, 'Professional Summary', 'PROFESSIONAL_SUMMARY', 2),
(11, '', '2025-07-04 13:07:40.616536', b'0', 'Consider adding: Specific dates (MM/YYYY), Company names, Job titles, Quantified achievements', '', 3, 'Work Experience', 'WORK_EXPERIENCE', 2),
(12, '', '2025-07-04 13:07:40.619656', b'0', 'Consider adding: Degree type, Institution name, Graduation date, GPA (if 3.5+)', '', 4, 'Education', 'EDUCATION', 2),
(13, 'Technical Skills: Programming, Data Analysis, Project Management\nSoft Skills: Leadership, Communication, Problem Solving, Team Collaboration\nTools & Technologies: Microsoft Office, Project Management Software\n', '2025-07-04 13:07:40.622662', b'1', 'Technical skills, soft skills, programming languages, tools', 'Médicales de Tunis', 5, 'Skills', 'SKILLS', 2),
(14, '', '2025-07-04 13:07:40.626033', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 6, 'Certifications', 'CERTIFICATIONS', 2),
(15, '', '2025-07-04 13:07:40.629327', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 7, 'Projects', 'PROJECTS', 2),
(16, '', '2025-07-04 13:07:40.632684', b'0', 'Consider adding specific details, dates, and quantifiable achievements to strengthen this section.', '', 8, 'Languages', 'LANGUAGES', 2);

-- --------------------------------------------------------

--
-- Table structure for table `cv_uploads`
--

CREATE TABLE `cv_uploads` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  `extracted_text` longtext,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `original_filename` varchar(255) NOT NULL,
  `processing_status` enum('FAILED','OPTIMIZED','PARSED','PROCESSING','UPLOADED') DEFAULT NULL,
  `stored_filename` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `cv_uploads`
--

INSERT INTO `cv_uploads` (`id`, `created_at`, `error_message`, `extracted_text`, `file_path`, `file_size`, `file_type`, `original_filename`, `processing_status`, `stored_filename`, `updated_at`, `user_id`) VALUES
(1, '2025-07-04 13:05:30.128894', NULL, 'Profil Professionnel \nParcours Professionnel \n54093503\nsafsoltani311998@gmail.com\nDar Chaabane El Fehri,\nNabeul\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2021-2022\nProtocoles d’analyses médicales \nMaitrise des normes d’hygiène\nTravail d’équipe \nCompétences \nAnglais \nFrançais\nLangues \nSAFA SOLTANI\nTechnic ienne de laborato i re  \n2 0 2 1\n–\n2 0 2 2\nTechnicienne de laboratoire \nCentre de maternité et de néontologie de tunis \nRéalisation des analyses hématologiques comme :\nNFS, GS et des analyses hormonales comme: FT4 et\nTSH...\n2 0 1 9\n-\n2 0 2 0\nSecrétaire médicale\nCabinet du médecin dentiste \nMaintenir et organiser de nombreux dossiers du\nbureau et accueil des patients.\nTechnicienne de laboratoire compétente avec une expérience\ndiversifiée dans différents paillaisses, dont les examens\nhématologiques, chimiques . Je suis dotée d’un excellent esprit\nd’équipe . À la recherche d\'un poste où je peux utiliser mes\ncompétences polyvalentes et mon sens aigu de l\'organisation.\nLicence en biotechnologies médicales.\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2018-2020\nPolyvalence \nFormation\nMaster en Biotechnologies, Management et\nIndustries de la Santé.\n', '1a487f19-727b-43b7-b214-7b1f77fbf38c.pdf', 123654, 'application/pdf', 'safe_resume.pdf', 'OPTIMIZED', '1a487f19-727b-43b7-b214-7b1f77fbf38c.pdf', '2025-07-04 13:05:35.565592', 2),
(2, '2025-07-04 13:07:38.183786', NULL, 'Profil Professionnel \nParcours Professionnel \n54093503\nsafsoltani311998@gmail.com\nDar Chaabane El Fehri,\nNabeul\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2021-2022\nProtocoles d’analyses médicales \nMaitrise des normes d’hygiène\nTravail d’équipe \nCompétences \nAnglais \nFrançais\nLangues \nSAFA SOLTANI\nTechnic ienne de laborato i re  \n2 0 2 1\n–\n2 0 2 2\nTechnicienne de laboratoire \nCentre de maternité et de néontologie de tunis \nRéalisation des analyses hématologiques comme :\nNFS, GS et des analyses hormonales comme: FT4 et\nTSH...\n2 0 1 9\n-\n2 0 2 0\nSecrétaire médicale\nCabinet du médecin dentiste \nMaintenir et organiser de nombreux dossiers du\nbureau et accueil des patients.\nTechnicienne de laboratoire compétente avec une expérience\ndiversifiée dans différents paillaisses, dont les examens\nhématologiques, chimiques . Je suis dotée d’un excellent esprit\nd’équipe . À la recherche d\'un poste où je peux utiliser mes\ncompétences polyvalentes et mon sens aigu de l\'organisation.\nLicence en biotechnologies médicales.\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2018-2020\nPolyvalence \nFormation\nMaster en Biotechnologies, Management et\nIndustries de la Santé.\n', 'a2413aab-dc39-4510-b66a-4781f6b96d2e.pdf', 123654, 'application/pdf', 'safe_resume.pdf', 'OPTIMIZED', 'a2413aab-dc39-4510-b66a-4781f6b96d2e.pdf', '2025-07-04 13:07:43.965242', 2);

-- --------------------------------------------------------

--
-- Table structure for table `optimization_results`
--

CREATE TABLE `optimization_results` (
  `id` bigint NOT NULL,
  `ai_analysis` longtext,
  `ats_score_after` int DEFAULT NULL,
  `ats_score_before` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `improvements` text,
  `keywords_added` text,
  `optimization_type` varchar(255) NOT NULL,
  `optimized_content` longtext,
  `original_content` longtext,
  `status` enum('COMPLETED','FAILED','IN_PROGRESS','PENDING') DEFAULT NULL,
  `suggestions` text,
  `cv_upload_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `optimization_results`
--

INSERT INTO `optimization_results` (`id`, `ai_analysis`, `ats_score_after`, `ats_score_before`, `created_at`, `improvements`, `keywords_added`, `optimization_type`, `optimized_content`, `original_content`, `status`, `suggestions`, `cv_upload_id`) VALUES
(1, '{\n    \"atsScore\": 70,\n    \"sections\": [\n        {\n            \"type\": \"CONTACT_INFO\",\n            \"content\": \"Contact information section detected\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"WORK_EXPERIENCE\",\n            \"content\": \"Work experience section found\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"EDUCATION\",\n            \"content\": \"Education section identified\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"SKILLS\",\n            \"content\": \"Skills section detected\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        }\n    ],\n    \"recommendations\": [\n        \"Use standard section headings for better ATS compatibility\",\n        \"Include quantified achievements and metrics\",\n        \"Add relevant industry keywords\",\n        \"Ensure consistent formatting throughout\"\n    ],\n    \"keywordSuggestions\": [\n        \"project management\",\n        \"team collaboration\",\n        \"problem solving\",\n        \"communication skills\"\n    ],\n    \"formatIssues\": [\n        \"Consider using bullet points for better readability\",\n        \"Ensure consistent date formatting\"\n    ]\n}\n', NULL, NULL, '2025-07-04 13:05:33.797987', NULL, NULL, 'OVERALL_ANALYSIS', NULL, 'Profil Professionnel \nParcours Professionnel \n54093503\nsafsoltani311998@gmail.com\nDar Chaabane El Fehri,\nNabeul\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2021-2022\nProtocoles d’analyses médicales \nMaitrise des normes d’hygiène\nTravail d’équipe \nCompétences \nAnglais \nFrançais\nLangues \nSAFA SOLTANI\nTechnic ienne de laborato i re  \n2 0 2 1\n–\n2 0 2 2\nTechnicienne de laboratoire \nCentre de maternité et de néontologie de tunis \nRéalisation des analyses hématologiques comme :\nNFS, GS et des analyses hormonales comme: FT4 et\nTSH...\n2 0 1 9\n-\n2 0 2 0\nSecrétaire médicale\nCabinet du médecin dentiste \nMaintenir et organiser de nombreux dossiers du\nbureau et accueil des patients.\nTechnicienne de laboratoire compétente avec une expérience\ndiversifiée dans différents paillaisses, dont les examens\nhématologiques, chimiques . Je suis dotée d’un excellent esprit\nd’équipe . À la recherche d\'un poste où je peux utiliser mes\ncompétences polyvalentes et mon sens aigu de l\'organisation.\nLicence en biotechnologies médicales.\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2018-2020\nPolyvalence \nFormation\nMaster en Biotechnologies, Management et\nIndustries de la Santé.\n', 'COMPLETED', NULL, 1),
(2, NULL, NULL, NULL, '2025-07-04 13:05:33.946817', NULL, NULL, 'SECTION_CONTACT_INFO', 'Content optimized for ATS compatibility with improved formatting and relevant keywords.', 'Email: safsoltani311998@gmail.com\n', 'COMPLETED', NULL, 1),
(3, NULL, NULL, NULL, '2025-07-04 13:05:34.898439', NULL, NULL, 'SECTION_SKILLS', 'Technical Skills: Programming, Data Analysis, Project Management\nSoft Skills: Leadership, Communication, Problem Solving, Team Collaboration\nTools & Technologies: Microsoft Office, Project Management Software\n', 'Médicales de Tunis', 'COMPLETED', NULL, 1),
(4, '{\n    \"atsScore\": 70,\n    \"sections\": [\n        {\n            \"type\": \"CONTACT_INFO\",\n            \"content\": \"Contact information section detected\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"WORK_EXPERIENCE\",\n            \"content\": \"Work experience section found\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"EDUCATION\",\n            \"content\": \"Education section identified\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        },\n        {\n            \"type\": \"SKILLS\",\n            \"content\": \"Skills section detected\",\n            \"isComplete\": true,\n            \"missingFields\": \"\"\n        }\n    ],\n    \"recommendations\": [\n        \"Use standard section headings for better ATS compatibility\",\n        \"Include quantified achievements and metrics\",\n        \"Add relevant industry keywords\",\n        \"Ensure consistent formatting throughout\"\n    ],\n    \"keywordSuggestions\": [\n        \"project management\",\n        \"team collaboration\",\n        \"problem solving\",\n        \"communication skills\"\n    ],\n    \"formatIssues\": [\n        \"Consider using bullet points for better readability\",\n        \"Ensure consistent date formatting\"\n    ]\n}\n', NULL, NULL, '2025-07-04 13:07:42.145056', NULL, NULL, 'OVERALL_ANALYSIS', NULL, 'Profil Professionnel \nParcours Professionnel \n54093503\nsafsoltani311998@gmail.com\nDar Chaabane El Fehri,\nNabeul\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2021-2022\nProtocoles d’analyses médicales \nMaitrise des normes d’hygiène\nTravail d’équipe \nCompétences \nAnglais \nFrançais\nLangues \nSAFA SOLTANI\nTechnic ienne de laborato i re  \n2 0 2 1\n–\n2 0 2 2\nTechnicienne de laboratoire \nCentre de maternité et de néontologie de tunis \nRéalisation des analyses hématologiques comme :\nNFS, GS et des analyses hormonales comme: FT4 et\nTSH...\n2 0 1 9\n-\n2 0 2 0\nSecrétaire médicale\nCabinet du médecin dentiste \nMaintenir et organiser de nombreux dossiers du\nbureau et accueil des patients.\nTechnicienne de laboratoire compétente avec une expérience\ndiversifiée dans différents paillaisses, dont les examens\nhématologiques, chimiques . Je suis dotée d’un excellent esprit\nd’équipe . À la recherche d\'un poste où je peux utiliser mes\ncompétences polyvalentes et mon sens aigu de l\'organisation.\nLicence en biotechnologies médicales.\nInstitut Supérieur des Technologies\nMédicales de Tunis \n2018-2020\nPolyvalence \nFormation\nMaster en Biotechnologies, Management et\nIndustries de la Santé.\n', 'COMPLETED', NULL, 2),
(5, NULL, NULL, NULL, '2025-07-04 13:07:42.320761', NULL, NULL, 'SECTION_CONTACT_INFO', 'Content optimized for ATS compatibility with improved formatting and relevant keywords.', 'Email: safsoltani311998@gmail.com\n', 'COMPLETED', NULL, 2),
(6, NULL, NULL, NULL, '2025-07-04 13:07:43.137828', NULL, NULL, 'SECTION_SKILLS', 'Technical Skills: Programming, Data Analysis, Project Management\nSoft Skills: Leadership, Communication, Problem Solving, Team Collaboration\nTools & Technologies: Microsoft Office, Project Management Software\n', 'Médicales de Tunis', 'COMPLETED', NULL, 2);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `created_at`, `email`, `name`, `phone`, `session_id`, `updated_at`) VALUES
(1, '2025-07-04 13:02:19.745393', 'user_3883922d@temp.com', 'Anonymous User', NULL, '249819d0-f439-441e-8203-8d5558e5a40c', '2025-07-04 13:02:19.745474'),
(2, '2025-07-04 13:05:20.821150', 'user_fcbc24df@temp.com', 'Anonymous User', NULL, '11b42360-6f9f-453e-a250-9cab7f8babc8', '2025-07-04 13:05:20.821212');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKq7jbjtm2yrr1bwpsma601bhe8` (`chat_session_id`);

--
-- Indexes for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKnbds8mvm10f5rs8rj2nlx98y9` (`session_id`),
  ADD KEY `FK82ky97glaomlmhjqae1d0esmy` (`user_id`);

--
-- Indexes for table `cv_sections`
--
ALTER TABLE `cv_sections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKb2ilqdadko4xqtcbhsnjr7ll2` (`cv_upload_id`);

--
-- Indexes for table `cv_uploads`
--
ALTER TABLE `cv_uploads`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK2wr2ynfs33hxphkoe0rhr66cd` (`user_id`);

--
-- Indexes for table `optimization_results`
--
ALTER TABLE `optimization_results`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKcawbfq222wfascjeu39fdk65s` (`cv_upload_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `cv_sections`
--
ALTER TABLE `cv_sections`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `cv_uploads`
--
ALTER TABLE `cv_uploads`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `optimization_results`
--
ALTER TABLE `optimization_results`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `FKq7jbjtm2yrr1bwpsma601bhe8` FOREIGN KEY (`chat_session_id`) REFERENCES `chat_sessions` (`id`);

--
-- Constraints for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  ADD CONSTRAINT `FK82ky97glaomlmhjqae1d0esmy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `cv_sections`
--
ALTER TABLE `cv_sections`
  ADD CONSTRAINT `FKb2ilqdadko4xqtcbhsnjr7ll2` FOREIGN KEY (`cv_upload_id`) REFERENCES `cv_uploads` (`id`);

--
-- Constraints for table `cv_uploads`
--
ALTER TABLE `cv_uploads`
  ADD CONSTRAINT `FK2wr2ynfs33hxphkoe0rhr66cd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `optimization_results`
--
ALTER TABLE `optimization_results`
  ADD CONSTRAINT `FKcawbfq222wfascjeu39fdk65s` FOREIGN KEY (`cv_upload_id`) REFERENCES `cv_uploads` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
