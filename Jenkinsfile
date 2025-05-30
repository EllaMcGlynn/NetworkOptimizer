pipeline {

    tools {
        maven "Maven 3.9.9"
    }

    environment {
        APP_NAME: "AURA"
        TAG: ""
    }

    stages{

        stage("Build") {
            steps {
                sh "mvn install -DskipTests"
            }
        }

        stage("Test") {
            steps {
                sh "mvn test"
            }
        }

        stage("Code Quality Analysis") {
            steps {
                echo "SonarQube Analysis"
            }
        }
    }
}