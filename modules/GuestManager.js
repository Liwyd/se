const readlineSync = require('readline-sync');

class GuestManager {
    showRegisteredStudentsCount(studentManager) {
        const totalStudents = studentManager.getAllStudents().length;
        const activeStudents = studentManager.getActiveStudents().length;
        
        console.log('\n--- تعداد دانشجویان ---');
        console.log(`تعداد کل دانشجویان ثبت نام کرده: ${totalStudents}`);
        console.log(`تعداد دانشجویان فعال: ${activeStudents}`);
        console.log(`تعداد دانشجویان غیرفعال: ${totalStudents - activeStudents}`);
    }

    searchBooks(bookManager) {
        console.log('\n--- جستجوی کتاب (مهمان) ---');
        const searchTerm = readlineSync.question('نام کتاب: ');
        
        const results = bookManager.searchByTitle(searchTerm);
        
        if (results.length === 0) {
            console.log('هیچ کتابی یافت نشد!');
        } else {
            console.log(`\nنتایج جستجو (${results.length} کتاب):`);
            results.forEach((book, index) => {
                console.log(`${index + 1}. ${book.title}`);
                console.log(`   نویسنده: ${book.author}`);
                console.log(`   سال نشر: ${book.publicationYear}`);
                console.log(`   شابک: ${book.isbn}`);
                console.log(`   دسته‌بندی: ${book.category}`);
                console.log('');
            });
        }
    }

    showStatistics(studentManager, bookManager, borrowManager) {
        console.log('\n--- اطلاعات آماری ---');
        
        const totalStudents = studentManager.getAllStudents().length;
        const totalBooks = bookManager.getAllBooks().length;
        const totalBorrows = borrowManager.getAllBorrows().length;
        const currentBorrows = borrowManager.getAllBorrows().filter(b => b.status === 'borrowed').length;
        
        console.log(`تعداد کل دانشجویان: ${totalStudents}`);
        console.log(`تعداد کل کتاب‌ها: ${totalBooks}`);
        console.log(`تعداد کل امانات: ${totalBorrows}`);
        console.log(`تعداد کتاب‌های در حال امانت: ${currentBorrows}`);
        
        // Show latest borrowed books
        const latestBorrows = borrowManager.getAllBorrows()
            .filter(b => b.status === 'borrowed')
            .sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
            .slice(0, 5);
        
        console.log('\nآخرین کتاب‌های امانت داده شده:');
        if (latestBorrows.length === 0) {
            console.log('هیچ کتابی در حال امانت نیست.');
        } else {
            latestBorrows.forEach((borrow, index) => {
                console.log(`${index + 1}. ${borrow.bookTitle} - ${borrow.studentUsername}`);
                console.log(`   تاریخ شروع: ${borrow.startDate}`);
            });
        }
    }
}

module.exports = GuestManager;
