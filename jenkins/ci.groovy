node('devops-aws') {
    stage('Clean up') {
        sh 'rm -rf *'
    }

    stage('Checkout') {
        def changeBranch = "change-${GERRIT_CHANGE_NUMBER}-${GERRIT_PATCHSET_NUMBER}"
        def scmVars = checkout scm
        git url: scmVars.GIT_URL
        sh "git fetch origin ${GERRIT_REFSPEC}:${changeBranch}"
        sh "git checkout ${changeBranch}"
    }

    stage('NPM Install') {
        echo 'NPM Install..'
        sh 'npm install'
    }

    stage('NPM Audit') {
        echo 'running npm audit..'
        sh 'npm audit --production'
    }

    stage('Format check') {
        echo 'Format checking..'
        sh './node_modules/.bin/ftnt-devops-ci check -f "**/*.{js,json}"'
    }

    stage('Eslint') {
        echo 'Eslinting..'
        sh './node_modules/.bin/ftnt-devops-ci check -l "**/*.js"'
    }

    stage('Test') {
        echo 'Testing..'
        sh 'npm test'
    }
}
