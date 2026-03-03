# How to Call restoreCategory from Browser

## ❌ Why Your Original URL Didn't Work

Your URL: `http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory?categoryId=123`

**Problem**: This treats `restoreCategory` as an HTTP endpoint with query parameters, but it's a **Callable Function** which requires:
1. POST method (not GET)
2. JSON body with data wrapped in `{"data": {...}}`
3. No query parameters

---

## ✅ Correct Ways to Call the Function

### Method 1: Use the HTML Test Page (Easiest!)

I've created a test page for you: **`test-restore-category.html`**

**Steps:**
1. Make sure your Firebase Emulator is running:
   ```powershell
   firebase emulators:start
   ```

2. Open the file in your browser:
   - Navigate to: `C:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\test-restore-category.html`
   - Or double-click the file

3. Enter your category ID: `64Z9iVMvOt71BagGyrst`

4. Click "🔄 Restore Category"

5. The page will show you the results!

---

### Method 2: Use PowerShell (Command Line)

```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"data":{"categoryId":"64Z9iVMvOt71BagGyrst"}}'
```

To see just the response content:
```powershell
$response = Invoke-WebRequest -Uri "http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"data":{"categoryId":"64Z9iVMvOt71BagGyrst"}}'
  
$response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
```

---

### Method 3: Use Postman

**Request Configuration:**
- **Method**: POST
- **URL**: `http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
    "data": {
      "categoryId": "64Z9iVMvOt71BagGyrst"
    }
  }
  ```

---

### Method 4: Use Firebase Emulator UI

1. Open: http://localhost:4000
2. Go to **Functions** tab
3. Find `restoreCategory` in the list
4. Click on it
5. Enter JSON data:
   ```json
   {
     "categoryId": "64Z9iVMvOt71BagGyrst"
   }
   ```
6. Click **Run**

---

### Method 5: Use JavaScript Console in Browser

Open browser console (F12) and run:

```javascript
fetch('http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    data: {
      categoryId: '64Z9iVMvOt71BagGyrst'
    }
  })
})
.then(response => response.json())
.then(data => {
  console.log('Success:', data);
})
.catch(error => {
  console.error('Error:', error);
});
```

---

## 📋 Expected Response

### Success Response:
```json
{
  "result": {
    "success": true,
    "categoryId": "64Z9iVMvOt71BagGyrst",
    "transactionsRestored": 5,
    "restoredTransactionIds": [
      "txn_id_1",
      "txn_id_2",
      "txn_id_3",
      "txn_id_4",
      "txn_id_5"
    ]
  }
}
```

### Error Response (Category Not Found):
```json
{
  "error": {
    "message": "Failed to restore category: Deleted category 64Z9iVMvOt71BagGyrst not found",
    "status": "INTERNAL"
  }
}
```

### Error Response (Not Authenticated):
```json
{
  "error": {
    "message": "Must be authenticated to restore categories",
    "status": "UNAUTHENTICATED"
  }
}
```

### Error Response (Invalid Argument):
```json
{
  "error": {
    "message": "categoryId is required",
    "status": "INVALID_ARGUMENT"
  }
}
```

---

## 🔍 Understanding Callable Functions

### Callable Function Structure:

**URL Format:**
```
http://[HOST]:[PORT]/[PROJECT_ID]/[REGION]/[FUNCTION_NAME]
```

**Example:**
```
http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory
```

**Request Requirements:**
- Method: `POST`
- Header: `Content-Type: application/json`
- Body: Data must be wrapped in `{"data": {...}}`

**Why wrap in "data"?**
Firebase Callable Functions automatically:
- Extract the `data` object from the request
- Handle authentication (if provided)
- Format the response in `{"result": {...}}` or `{"error": {...}}`

---

## 🛠️ Troubleshooting

### Error: "Cannot connect to localhost:5001"
**Solution:** Start the Firebase Emulator
```powershell
firebase emulators:start
```

### Error: "UNAUTHENTICATED"
**Solution:** Callable functions require authentication. In emulator mode:
- Authentication is usually bypassed for testing
- If you still get this error, check your function implementation
- For production, you need to pass an auth token

### Error: "Deleted category not found"
**Solution:** Make sure the category exists in `deleted/categories/items/{categoryId}`
- Check in Firestore Emulator UI: http://localhost:4000
- Navigate to: Firestore → deleted → categories → items
- Verify your categoryId exists there

### Error: "Bad Request" or "INVALID_ARGUMENT"
**Solution:** Check your request format:
- ✅ Correct: `{"data": {"categoryId": "..."}}`
- ❌ Wrong: `{"categoryId": "..."}`
- ❌ Wrong: Query parameters `?categoryId=...`

---

## 📊 Testing Workflow

### Complete Test Flow:

1. **Start Emulator:**
   ```powershell
   firebase emulators:start
   ```

2. **Delete a Category** (to create test data):
   - Open Firestore UI: http://localhost:4000
   - Go to: `globalCategories/{categoryId}`
   - Update: `isDeleted = true`
   - Wait a few seconds for Cloud Function to process

3. **Verify Deletion:**
   - Check: `deleted/categories/items/{categoryId}` exists
   - Check: Related transactions moved to `deleted/transactions/items`

4. **Restore Category:**
   - Use any method above (HTML page, PowerShell, etc.)
   - Pass the categoryId

5. **Verify Restoration:**
   - Check: Category is back in `globalCategories/{categoryId}`
   - Check: `isDeleted = false`
   - Check: Transactions are back in `users/{userId}/{expenses|incomes}`
   - Check: `deleted/categories/items/{categoryId}` is gone
   - Check: Transactions removed from `deleted/transactions/items`

---

## 💡 Quick Tips

1. **Always use the HTML test page** for easiest testing
2. **Check emulator logs** in the terminal where you ran `firebase emulators:start`
3. **Use the Firestore UI** at http://localhost:4000 to inspect data
4. **Watch the Functions logs** for detailed error messages
5. **Remember**: Data must be wrapped in `{"data": {...}}`

---

## 📞 Need Help?

If you're still having issues:

1. Check that the emulator is running
2. Verify the category exists in `deleted/categories/items`
3. Look at the emulator terminal for error logs
4. Use the HTML test page - it has built-in error handling
5. Check the QUICK_REFERENCE.md for more examples

---

## 🎯 Summary

**DON'T DO THIS:**
```
❌ http://127.0.0.1:5001/.../restoreCategory?categoryId=123
```

**DO THIS INSTEAD:**
```
✅ Use test-restore-category.html
✅ POST with JSON body: {"data": {"categoryId": "123"}}
✅ Use Emulator UI Functions tab
```

The HTML test page (`test-restore-category.html`) is the easiest way to test!
