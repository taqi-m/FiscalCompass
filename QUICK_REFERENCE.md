# Category Deletion Functions - Quick Reference

## 🎯 What Was Implemented

### Cloud Functions Created:
1. **`onCategoryUpdate`** - Automatic trigger when category is deleted
2. **`restoreCategory`** - Callable function to restore deleted categories

### Files Modified:
- ✅ `functions/index.js` - Main implementation
- ✅ `firestore.rules` - Security rules updated
- ✅ `firestore.indexes.json` - Performance indexes added

## 📋 How It Works

### Deletion Flow:
```
1. Update category: isDeleted = true
   ↓
2. Cloud Function triggers automatically
   ↓
3. Category moved to: deleted/categories/items/{categoryId}
   ↓
4. Find all transactions with categoryFirestoreId = {categoryId}
   ↓
5. Move transactions to: deleted/transactions/items/{transactionId}
   ↓
6. Delete from original locations
```

### Restoration Flow:
```
1. Call restoreCategory({ categoryId })
   ↓
2. Retrieve from: deleted/categories/items/{categoryId}
   ↓
3. Restore to: globalCategories/{categoryId} with isDeleted = false
   ↓
4. Find all transactions in deleted/transactions/items
   ↓
5. Restore to: users/{userId}/{expenses|incomes}/{transactionId}
   ↓
6. Delete from deleted collections
```

## 🚀 Quick Start (Emulator)

### 1. Start Emulator:
```powershell
firebase emulators:start
```

### 2. Delete a Category:
```javascript
// In your Android app or test script
db.collection('globalCategories').doc(categoryId).update({
  isDeleted: true
});
// Cloud Function handles the rest automatically!
```

### 3. Restore a Category:

#### From Browser (Testing):
Open the test page: `test-restore-category.html` in your browser

Or use PowerShell:
```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"data":{"categoryId":"YOUR_CATEGORY_ID"}}'
```

Or use cURL (Git Bash/Linux/Mac):
```bash
curl -X POST http://127.0.0.1:5001/fiscalcompass-48814/us-central1/restoreCategory \
  -H "Content-Type: application/json" \
  -d '{"data":{"categoryId":"YOUR_CATEGORY_ID"}}'
```

#### From Android App:
```kotlin
val functions = Firebase.functions
val restoreCategory = functions.getHttpsCallable("restoreCategory")

restoreCategory.call(hashMapOf("categoryId" to categoryId))
    .addOnSuccessListener { result ->
        val data = result.data as Map<String, Any>
        println("Restored ${data["transactionsRestored"]} transactions")
    }
```

## 📁 Database Paths

### Before Deletion:
- Category: `globalCategories/{categoryId}`
- Transactions: `users/{userId}/expenses/{transactionId}`
- Transactions: `users/{userId}/incomes/{transactionId}`

### After Deletion:
- Category: `deleted/categories/items/{categoryId}`
- Transactions: `deleted/transactions/items/{transactionId}`

### After Restoration:
- Back to original paths with `isDeleted = false`

## 🔒 Security

- ✅ Only admins can access `deleted/*` collections
- ✅ `restoreCategory` requires authentication
- ✅ Users can only access their own transactions
- ✅ Everyone can read globalCategories

## 📊 Performance

- ✅ Batched writes (500 operations per batch)
- ✅ Composite indexes for fast queries
- ✅ Efficient transaction processing per user

## 🔍 Monitoring

### View Logs (Emulator):
- Check terminal where emulator is running
- Or visit: http://localhost:4000

### View Logs (Production):
```powershell
firebase functions:log
```

## ⚠️ Important Notes

1. **Soft Delete Pattern**: Categories are moved, not permanently deleted
2. **Cascading Delete**: All related transactions are automatically handled
3. **Metadata Preserved**: Original paths stored for restoration
4. **Batch Processing**: Handles large datasets efficiently
5. **Error Logging**: Comprehensive logs for debugging

## 🧪 Testing Checklist

Before deploying to production:

- [ ] Test category deletion with transactions
- [ ] Test category deletion without transactions
- [ ] Test category restoration
- [ ] Test with multiple users
- [ ] Test with high volume (1000+ transactions)
- [ ] Test error cases (invalid IDs, etc.)
- [ ] Verify logs are helpful
- [ ] Check Firestore rules work correctly

## 🚢 Deployment

### Deploy to Emulator (Auto):
```powershell
firebase emulators:start
```

### Deploy to Production:
```powershell
# Deploy everything
firebase deploy

# Or deploy only functions
firebase deploy --only functions

# Or deploy specific functions
firebase deploy --only functions:onCategoryUpdate,functions:restoreCategory
```

## 📚 Documentation Files

- `CATEGORY_DELETION_FUNCTIONS.md` - Detailed documentation
- `TESTING_GUIDE.md` - Comprehensive testing guide
- This file - Quick reference

## 🆘 Troubleshooting

### Function not triggering?
- Check if `isDeleted` actually changed from false to true
- Check emulator logs for errors
- Verify function is deployed

### Transactions not moving?
- Check if composite indexes are created
- Verify `categoryFirestoreId` matches exactly
- Check if transactions have `isDeleted: false`

### Restore failing?
- Verify category exists in `deleted/categories/items`
- Check authentication
- Review function logs for specific error

### Performance issues?
- Check if indexes are created
- Monitor batch commit logs
- Consider function timeout limits

## 💡 Tips

1. Always test in emulator first
2. Check logs after every operation
3. Use Firestore Emulator UI to inspect data
4. Keep test data handy for quick validation
5. Monitor function execution times in production

## 📞 Support

For issues or questions:
1. Check the detailed documentation
2. Review function logs
3. Test in emulator with sample data
4. Check Firestore rules are correct
