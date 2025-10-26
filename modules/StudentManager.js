const readlineSync = require('readline-sync');

class StudentManager {
    constructor() {
        this.students = [];
        this.nextId = 1;
    }

    register() {
        console.log('\n--- ثبت نام دانشجو ---');
        const username = readlineSync.question('نام کاربری: ');
        
        // Check if username already exists
        if (this.students.find(s => s.username === username)) {
            console.log('این نام کاربری قبلاً استفاده شده است!');
            return null;
        }

        const password = readlineSync.question('رمز عبور: ', { hideEchoBack: true });
        const confirmPassword = readlineSync.question('تکرار رمز عبور: ', { hideEchoBack: true });

        if (password !== confirmPassword) {
            console.log('رمزهای عبور مطابقت ندارند!');
            return null;
        }

        const student = {
            id: this.nextId++,
            username: username,
            password: password,
            isActive: true,
            registrationDate: new Date()
        };

        this.students.push(student);
        console.log('ثبت نام با موفقیت انجام شد!');
        return student;
    }

    login(username, password) {
        const student = this.students.find(s => s.username === username && s.password === password && s.isActive);
        return student || null;
    }

    searchBooks(bookManager) {
        console.log('\n--- جستجوی کتاب ---');
        console.log('1. جستجو بر اساس عنوان');
        console.log('2. جستجو بر اساس سال نشر');
        console.log('3. جستجو بر اساس نام نویسنده');
        console.log('4. جستجوی ترکیبی');

        const choice = readlineSync.question('نوع جستجو را انتخاب کنید: ');

        let searchTerm;
        let results = [];

        switch (choice) {
            case '1':
                searchTerm = readlineSync.question('عنوان کتاب: ');
                results = bookManager.searchByTitle(searchTerm);
                break;
            case '2':
                searchTerm = readlineSync.question('سال نشر: ');
                results = bookManager.searchByYear(searchTerm);
                break;
            case '3':
                searchTerm = readlineSync.question('نام نویسنده: ');
                results = bookManager.searchByAuthor(searchTerm);
                break;
            case '4':
                const title = readlineSync.question('عنوان کتاب (اختیاری): ');
                const year = readlineSync.question('سال نشر (اختیاری): ');
                const author = readlineSync.question('نام نویسنده (اختیاری): ');
                results = bookManager.combinedSearch(title, year, author);
                break;
            default:
                console.log('انتخاب نامعتبر!');
                return;
        }

        if (results.length === 0) {
            console.log('هیچ کتابی یافت نشد!');
        } else {
            console.log(`\nنتایج جستجو (${results.length} کتاب):`);
            results.forEach((book, index) => {
                console.log(`${index + 1}. ${book.title} - ${book.author} (${book.publicationYear})`);
                console.log(`   وضعیت: ${book.isAvailable ? 'موجود' : 'امانت داده شده'}`);
                console.log(`   شناسه: ${book.id}`);
                console.log('');
            });
        }
    }

    requestBorrow(bookManager, borrowManager) {
        console.log('\n--- ثبت درخواست امانت ---');
        const bookId = readlineSync.question('شناسه کتاب: ');
        
        const book = bookManager.getBookById(parseInt(bookId));
        if (!book) {
            console.log('کتاب مورد نظر یافت نشد!');
            return;
        }

        if (!book.isAvailable) {
            console.log('این کتاب در حال حاضر در امانت است!');
            return;
        }

        const startDate = readlineSync.question('تاریخ شروع امانت (YYYY-MM-DD): ');
        const endDate = readlineSync.question('تاریخ پایان امانت (YYYY-MM-DD): ');

        // Validate dates
        const start = new Date(startDate);
        const end = new Date(endDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (start < today) {
            console.log('تاریخ شروع نمی‌تواند قبل از امروز باشد!');
            return;
        }

        if (end <= start) {
            console.log('تاریخ پایان باید بعد از تاریخ شروع باشد!');
            return;
        }

        const request = borrowManager.createBorrowRequest(this.currentUser.id, book.id, startDate, endDate);
        if (request) {
            console.log('درخواست امانت با موفقیت ثبت شد!');
        } else {
            console.log('خطا در ثبت درخواست!');
        }
    }

    showBorrowRequests(borrowManager) {
        console.log('\n--- درخواست‌های امانت شما ---');
        const requests = borrowManager.getStudentRequests(this.currentUser.id);
        
        if (requests.length === 0) {
            console.log('هیچ درخواست امانتی ندارید.');
            return;
        }

        requests.forEach((request, index) => {
            console.log(`${index + 1}. کتاب: ${request.bookTitle}`);
            console.log(`   تاریخ شروع: ${request.startDate}`);
            console.log(`   تاریخ پایان: ${request.endDate}`);
            console.log(`   وضعیت: ${request.status}`);
            console.log('');
        });
    }

    getStudentById(id) {
        return this.students.find(s => s.id === id);
    }

    getAllStudents() {
        return this.students;
    }

    getActiveStudents() {
        return this.students.filter(s => s.isActive);
    }

    toggleStudentStatus(studentId) {
        const student = this.students.find(s => s.id === studentId);
        if (student) {
            student.isActive = !student.isActive;
            return student.isActive;
        }
        return null;
    }
}

module.exports = StudentManager;
