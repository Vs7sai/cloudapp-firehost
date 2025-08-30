module.exports = [
  {
    "id": 1,
    "text": "What is Git cherry-pick and how do you use it?",
    "explanation": "Git cherry-pick allows you to apply a specific commit from one branch to another branch. It's useful for applying bug fixes or features without merging entire branches.\n\nUsage: git cherry-pick <commit-hash>\n\nAdvanced usage:\n• git cherry-pick -x: Adds source commit info to commit message\n• git cherry-pick --no-commit: Stages changes without committing\n• git cherry-pick --continue: Continue after resolving conflicts\n• git cherry-pick --abort: Abort the cherry-pick operation\n\nBest practices:\n• Use when you need specific commits, not entire feature branches\n• Be careful with commits that depend on other commits\n• Consider using cherry-pick for hotfixes across release branches"
  },
  {
    "id": 2,
    "text": "Explain Git hooks and their use cases",
    "explanation": "Git hooks are scripts that run automatically at specific points in the Git workflow. They're stored in the .git/hooks directory.\n\nPre-commit hooks:\n• Run before commit is created\n• Use cases: Code formatting, linting, tests, pre-commit checks\n• Can prevent commit if checks fail\n\nPost-commit hooks:\n• Run after commit is created\n• Use cases: Notifications, deployment triggers, logging\n\nPre-push hooks:\n• Run before pushing to remote\n• Use cases: Integration tests, security scans\n\nPost-merge hooks:\n• Run after merge operations\n• Use cases: Dependency updates, build processes\n\nImplementation: Create executable scripts in .git/hooks/ with names like pre-commit, post-commit, etc."
  },
  {
    "id": 3,
    "text": "What is Git submodules and when to use them?",
    "explanation": "Git submodules allow you to include other Git repositories as subdirectories within your main repository.\n\nUse cases:\n• Including third-party libraries\n• Sharing common code between projects\n• Managing complex project dependencies\n\nBasic commands:\n• git submodule add <repository> <path>: Add a submodule\n• git submodule init: Initialize submodules\n• git submodule update: Update submodules to latest commits\n• git submodule foreach: Run commands in all submodules\n\nPros:\n• Keep external dependencies in sync\n• Version control for external code\n• Clean separation of concerns\n\nCons:\n• Complex workflow for contributors\n• Can cause confusion if not managed properly\n• Requires additional Git knowledge"
  }
];
