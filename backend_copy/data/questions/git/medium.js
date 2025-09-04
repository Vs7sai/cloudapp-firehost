module.exports = [
  {
    "id": 1,
    "text": "What is Git branching and merging?",
    "explanation": "Git branching allows you to create separate lines of development. Each branch represents an independent line of work.\n\nKey concepts:\n• Main/Master branch: The primary branch\n• Feature branches: For developing new features\n• Hotfix branches: For urgent fixes\n\nMerging combines changes from different branches:\n• Fast-forward merge: When no conflicts exist\n• Merge commit: Creates a merge commit when branches diverge\n• Rebase: Replays commits on top of another branch"
  },
  {
    "id": 2,
    "text": "How do you resolve Git merge conflicts?",
    "explanation": "Merge conflicts occur when Git can't automatically merge changes. Here's how to resolve them:\n\n1. Identify conflicted files: git status shows files with conflicts\n2. Open conflicted files and look for conflict markers (<<<<<<< HEAD, =======, >>>>>>> branch)\n3. Edit files to resolve conflicts manually\n4. Remove conflict markers\n5. Stage resolved files: git add <filename>\n6. Complete the merge: git commit\n\nBest practice: Always pull latest changes before starting work to minimize conflicts."
  },
  {
    "id": 3,
    "text": "What is Git rebase and when to use it?",
    "explanation": "Git rebase is a way to integrate changes from one branch into another by moving or combining commits.\n\nRebase vs Merge:\n• Rebase: Creates a linear history, moves commits to new base\n• Merge: Preserves branch history, creates merge commits\n\nWhen to use rebase:\n• Before merging feature branches to main\n• To keep commit history clean and linear\n• When working on personal feature branches\n\nWhen NOT to use rebase:\n• On shared/public branches\n• When commits have already been pushed to remote"
  }
];
