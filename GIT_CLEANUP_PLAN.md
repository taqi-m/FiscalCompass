# Git VCS Cleanup Plan
## Remove `.idea/`, `functions/`, `EMULATOR_SETUP.md`, and Firebase Files from All Branches

---

## 🎯 Objective

Permanently remove the following from Git tracking — locally and remotely, across **all branches** — without deleting the files from disk:

| Target | Type |
|---|---|
| `.idea/` | Folder (IDE config) |
| `functions/` | Folder (Firebase Cloud Functions) |
| `EMULATOR_SETUP.md` | File (project root) |
| `firebase.json` | File (project root) |
| `firestore.indexes.json` | File (project root) |
| `firestore.rules` | File (project root, if tracked) |
| `.firebaserc` | File (project root, if tracked) |

---

## ⚠️ Before You Start — Critical Warnings

1. **History rewrite is destructive and irreversible.** This rewrites every commit across every branch. There is no undo without a backup.
2. **Coordinate with your team.** After force-pushing, every team member's local clone will be out of sync. They MUST re-clone or hard-reset.
3. **Disable branch protection rules** on GitHub/GitLab for all protected branches (e.g., `main`, `develop`) before force-pushing. Re-enable them afterward.
4. **Do NOT push any new commits** between running `git filter-repo` and force-pushing. Do the steps in one session.

---

## 📋 Prerequisites

### 1. Install Python 3.x
`git-filter-repo` requires Python. Verify:
```powershell
python --version
```
If not installed: https://www.python.org/downloads/

### 2. Install git-filter-repo
```powershell
pip install git-filter-repo
```
Verify:
```powershell
git filter-repo --version
```

### 3. Back Up the Repository
Copy the entire project folder to a safe location before proceeding:
```powershell
Copy-Item -Recurse -Force "C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass" "C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass_BACKUP"
```

---

## 📝 Step 1 — Audit Currently Tracked Files

Run these to confirm exactly what is currently tracked:

```powershell
cd "C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass"

# Check .idea folder
git --no-pager ls-files --cached -- ".idea"

# Check functions folder
git --no-pager ls-files --cached -- "functions"

# Check EMULATOR_SETUP.md
git --no-pager ls-files --cached -- "EMULATOR_SETUP.md"

# Check Firebase root files
git --no-pager ls-files --cached -- "firebase.json" "firestore.indexes.json" "firestore.rules" ".firebaserc"
```

> **Expected output:** Any file listed here is tracked and will be removed in subsequent steps.

---

## 📝 Step 2 — Update `.gitignore`

Add the following block to your `.gitignore` at the project root.  
> Your `.gitignore` already covers some of these, but add these explicit top-level rules to ensure full coverage:

```gitignore
# === CLEANUP: Removed from tracking ===
/.idea/
/functions/
/EMULATOR_SETUP.md
/firebase.json
/firestore.rules
/firestore.indexes.json
/.firebaserc
```

Commit the `.gitignore` update **before** running `git rm --cached` in Step 3:

```powershell
git add .gitignore
git commit -m "chore: update .gitignore to exclude .idea, functions, firebase config files"
```

---

## 📝 Step 3 — Untrack Files from the Current Index (Without Deleting from Disk)

These commands remove the files from Git's index (so they stop being tracked going forward) while keeping them on your local disk:

```powershell
cd "C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass"

# Remove .idea folder from tracking
git rm -r --cached .idea

# Remove functions folder from tracking
git rm -r --cached functions

# Remove EMULATOR_SETUP.md from tracking
git rm --cached EMULATOR_SETUP.md

# Remove Firebase root files from tracking
git rm --cached firebase.json
git rm --cached firestore.indexes.json

# Run these only if they are tracked (check output from Step 1):
git rm --cached firestore.rules
git rm --cached .firebaserc
```

> **Note:** If a file is NOT tracked, `git rm --cached` will print an error like `fatal: pathspec '...' did not match any files`. That's safe to ignore.

Commit the removal:

```powershell
git add -A
git commit -m "chore: remove .idea, functions, EMULATOR_SETUP.md, firebase config from tracking"
```

---

## 📝 Step 4 — Rewrite Full Git History with `git filter-repo`

This step purges the target paths from **every commit in every branch**. This is necessary so the files disappear from history, not just from the latest commit.

> ⚠️ `git filter-repo` modifies the repo in-place and removes the `origin` remote as a safety measure. You will re-add it in Step 6.

### First, note your remote URL before running filter-repo:
```powershell
git --no-pager remote -v
```
**Save this URL — you will need it in Step 6.**

### Run git filter-repo to purge all target paths:

```powershell
cd "C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass"

git filter-repo --invert-paths `
  --path ".idea/" `
  --path "functions/" `
  --path "EMULATOR_SETUP.md" `
  --path "firebase.json" `
  --path "firestore.indexes.json" `
  --path "firestore.rules" `
  --path ".firebaserc" `
  --force
```

> **What `--invert-paths` does:** It keeps everything EXCEPT the listed paths — effectively deleting those paths from every commit in history.

> **What `--force` does:** Allows running on a repo that is not a fresh clone (has existing changes or remote refs).

---

## 📝 Step 5 — Verify Removal from History

After `git filter-repo` completes, verify the files are gone from all history:

```powershell
# Each of these should return NO output if successful
git --no-pager log --all --oneline --diff-filter=A -- ".idea/"
git --no-pager log --all --oneline --diff-filter=A -- "functions/"
git --no-pager log --all --oneline --diff-filter=A -- "EMULATOR_SETUP.md"
git --no-pager log --all --oneline --diff-filter=A -- "firebase.json"
git --no-pager log --all --oneline --diff-filter=A -- "firestore.indexes.json"

# Also confirm nothing in the current index
git --no-pager ls-files -- ".idea" "functions" "EMULATOR_SETUP.md" "firebase.json" "firestore.indexes.json"
```

All commands should return **empty output** — meaning those paths exist nowhere in history.

---

## 📝 Step 6 — Re-Add the Remote (git filter-repo removes it)

`git filter-repo` removes the `origin` remote as a safety measure. Re-add it:

```powershell
# Replace <YOUR_REMOTE_URL> with the URL you saved in Step 4
git remote add origin <YOUR_REMOTE_URL>

# Example for GitHub:
# git remote add origin https://github.com/yourusername/FiscalCompass.git
# OR for SSH:
# git remote add origin git@github.com:yourusername/FiscalCompass.git

# Verify:
git --no-pager remote -v
```

---

## 📝 Step 7 — Force-Push All Branches to Remote

Fetch the remote refs first, then force-push every local branch:

```powershell
# Fetch remote state (to know what branches exist remotely)
git fetch origin

# List all local branches
git --no-pager branch

# Force-push each local branch to remote using --force-with-lease
# (safer than --force: fails if someone pushed new commits since your last fetch)

# For your main/default branch (replace 'main' with your branch name if different):
git push origin main --force-with-lease

# For each additional branch, repeat:
# git push origin <branch-name> --force-with-lease

# To push ALL local branches at once (use with caution):
git push origin --all --force-with-lease
```

> **Tip:** Run `git --no-pager branch` to see all local branch names before pushing.

### If `--force-with-lease` fails (because remote has diverged):
Use `--force` only if you are certain no one else has pushed to that branch:
```powershell
git push origin --all --force
```

---

## 📝 Step 8 — Delete Stale Remote Tracking Refs Locally

After force-pushing, clean up any stale remote-tracking references:

```powershell
git remote prune origin
git --no-pager branch -a
```

---

## 📝 Step 9 — Team Coordination (MANDATORY)

After the force-push, **every team member must do one of the following:**

### Option A — Re-clone (Recommended, Safest)
```bash
# Team member runs on their machine:
git clone <REMOTE_URL>
```

### Option B — Hard Reset (If they don't want to re-clone)
```bash
# Team member runs on their machine for each branch they use:
git fetch origin
git checkout main
git reset --hard origin/main
```

> ⚠️ Any uncommitted local changes will be lost with `reset --hard`. Team members should stash or back up local work first.

---

## 📝 Step 10 — Final Verification

Confirm the files are fully purged and `.gitignore` is working:

```powershell
# 1. These should all return empty:
git --no-pager ls-files ".idea" "functions" "EMULATOR_SETUP.md" "firebase.json"

# 2. Create a test file to confirm .gitignore works:
New-Item -ItemType File "test_gitignore_check.txt" -Value "test"
git status   # Should NOT show .idea/, functions/, EMULATOR_SETUP.md, firebase.json as untracked
Remove-Item "test_gitignore_check.txt"
```

---

## 🗂️ Summary Checklist

| Step | Action | Done? |
|---|---|---|
| 0 | Backup repo | ☐ |
| 1 | Install prerequisites (Python, git-filter-repo) | ☐ |
| 2 | Audit tracked files | ☐ |
| 3 | Update `.gitignore` and commit | ☐ |
| 4 | `git rm --cached` all target paths and commit | ☐ |
| 5 | Save remote URL | ☐ |
| 6 | Run `git filter-repo --invert-paths ...` | ☐ |
| 7 | Verify removal from history | ☐ |
| 8 | Re-add `origin` remote | ☐ |
| 9 | Disable branch protections on GitHub/GitLab | ☐ |
| 10 | Force-push all branches | ☐ |
| 11 | Re-enable branch protections | ☐ |
| 12 | Notify team members to re-clone or hard-reset | ☐ |

---

## 🚨 Troubleshooting

| Problem | Solution |
|---|---|
| `git filter-repo: command not found` | Run `pip install git-filter-repo` and ensure Python Scripts dir is in PATH |
| `fatal: refusing to work with uncommitted changes` | Commit or stash all pending changes before running filter-repo |
| `remote rejected (protected branch)` | Disable branch protection on GitHub/GitLab Settings → Branches |
| `error: failed to push some refs` | Use `--force` instead of `--force-with-lease` if remote has diverged |
| Files reappear after push | Ensure `.gitignore` was updated and committed BEFORE the filter-repo run |
| `git filter-repo` removes origin remote | Expected behavior — re-add origin manually (Step 6) |

---

*Generated: 2026-03-04*

