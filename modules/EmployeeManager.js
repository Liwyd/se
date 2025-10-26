const readlineSync = require('readline-sync');

class EmployeeManager {
    constructor() {
        this.employees = [];
        this.nextId = 1;
    }

    addEmployee(username, password) {
        // Check if username already exists
        if (this.employees.find(e => e.username === username)) {
            console.log('این نام کاربری قبلاً استفاده شده است!');
            return null;
        }

        const employee = {
            id: this.nextId++,
            username: username,
            password: password,
            addedDate: new Date()
        };

        this.employees.push(employee);
        console.log('کارمند با موفقیت اضافه شد!');
        return employee;
    }

    login(username, password) {
        const employee = this.employees.find(e => e.username === username && e.password === password);
        return employee || null;
    }

    changePassword(employee) {
        console.log('\n--- تغییر رمز عبور ---');
        const currentPassword = readlineSync.question('رمز عبور فعلی: ', { hideEchoBack: true });
        
        if (currentPassword !== employee.password) {
            console.log('رمز عبور فعلی اشتباه است!');
            return false;
        }

        const newPassword = readlineSync.question('رمز عبور جدید: ', { hideEchoBack: true });
        const confirmPassword = readlineSync.question('تکرار رمز عبور جدید: ', { hideEchoBack: true });

        if (newPassword !== confirmPassword) {
            console.log('رمزهای عبور مطابقت ندارند!');
            return false;
        }

        employee.password = newPassword;
        console.log('رمز عبور با موفقیت تغییر یافت!');
        return true;
    }

    addBook(bookManager, employee) {
        return bookManager.addBook(employee);
    }

    searchAndEditBook(bookManager) {
        console.log('\n--- جستجو و ویرایش کتاب ---');
        const searchTerm = readlineSync.question('عنوان یا نویسنده کتاب: ');
        
        const results = bookManager.books.filter(b => 
            b.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            b.author.toLowerCase().includes(searchTerm.toLowerCase())
        );

        if (results.length === 0) {
            console.log('هیچ کتابی یافت نشد!');
            return;
        }

        console.log('\nنتایج جستجو:');
        results.forEach((book, index) => {
            console.log(`${index + 1}. ${book.title} - ${book.author} (${book.publicationYear})`);
            console.log(`   شناسه: ${book.id}`);
        });

        const choice = readlineSync.question('شماره کتاب برای ویرایش (0 برای لغو): ');
        const bookIndex = parseInt(choice) - 1;

        if (bookIndex >= 0 && bookIndex < results.length) {
            bookManager.editBook(results[bookIndex].id);
        }
    }

    reviewBorrowRequests(borrowManager) {
        console.log('\n--- بررسی درخواست‌های امانت ---');
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        const pendingRequests = borrowManager.getPendingRequests().filter(request => {
            const startDate = new Date(request.startDate);
            startDate.setHours(0, 0, 0, 0);
            return startDate <= today;
        });

        if (pendingRequests.length === 0) {
            console.log('هیچ درخواست امانتی برای بررسی وجود ندارد.');
            return;
        }

        console.log(`درخواست‌های قابل بررسی (${pendingRequests.length} مورد):`);
        pendingRequests.forEach((request, index) => {
            console.log(`${index + 1}. دانشجو: ${request.studentUsername}`);
            console.log(`   کتاب: ${request.bookTitle}`);
            console.log(`   تاریخ شروع: ${request.startDate}`);
            console.log(`   تاریخ پایان: ${request.endDate}`);
            console.log('');
        });

        const choice = readlineSync.question('شماره درخواست برای تایید (0 برای لغو): ');
        const requestIndex = parseInt(choice) - 1;

        if (requestIndex >= 0 && requestIndex < pendingRequests.length) {
            const request = pendingRequests[requestIndex];
            const action = readlineSync.question('تایید (y) یا رد (n): ');
            
            if (action.toLowerCase() === 'y') {
                borrowManager.approveRequest(request.id, this.currentUser.id);
                console.log('درخواست تایید شد!');
            } else if (action.toLowerCase() === 'n') {
                borrowManager.rejectRequest(request.id);
                console.log('درخواست رد شد!');
            }
        }
    }

    showStudentReport(studentManager, borrowManager) {
        console.log('\n--- گزارش دانشجو ---');
        const studentId = readlineSync.question('شناسه دانشجو: ');
        
        const student = studentManager.getStudentById(parseInt(studentId));
        if (!student) {
            console.log('دانشجو مورد نظر یافت نشد!');
            return;
        }

        const borrowHistory = borrowManager.getStudentHistory(parseInt(studentId));
        
        console.log(`\nگزارش دانشجو: ${student.username}`);
        console.log(`وضعیت: ${student.isActive ? 'فعال' : 'غیرفعال'}`);
        console.log(`تاریخ ثبت نام: ${student.registrationDate.toLocaleDateString('fa-IR')}`);
        
        console.log('\nآمار امانات:');
        console.log(`تعداد کل امانات: ${borrowHistory.length}`);
        
        const notReturned = borrowHistory.filter(b => b.status === 'borrowed').length;
        console.log(`تعداد کتاب‌های تحویل داده نشده: ${notReturned}`);
        
        const delayed = borrowHistory.filter(b => b.isDelayed).length;
        console.log(`تعداد امانت‌های با تاخیر: ${delayed}`);

        console.log('\nتاریخچه امانات:');
        if (borrowHistory.length === 0) {
            console.log('هیچ امانتی ثبت نشده است.');
        } else {
            borrowHistory.forEach((borrow, index) => {
                console.log(`${index + 1}. ${borrow.bookTitle}`);
                console.log(`   تاریخ شروع: ${borrow.startDate}`);
                console.log(`   تاریخ پایان: ${borrow.endDate}`);
                console.log(`   وضعیت: ${borrow.status}`);
                if (borrow.isDelayed) {
                    console.log(`   ⚠️ تاخیر در تحویل`);
                }
                console.log('');
            });
        }
    }

    toggleStudentStatus(studentManager) {
        console.log('\n--- فعال/غیرفعال کردن دانشجو ---');
        const studentId = readlineSync.question('شناسه دانشجو: ');
        
        const student = studentManager.getStudentById(parseInt(studentId));
        if (!student) {
            console.log('دانشجو مورد نظر یافت نشد!');
            return;
        }

        const newStatus = studentManager.toggleStudentStatus(parseInt(studentId));
        if (newStatus !== null) {
            console.log(`دانشجو ${newStatus ? 'فعال' : 'غیرفعال'} شد.`);
        }
    }

    returnBook(borrowManager) {
        console.log('\n--- دریافت کتاب ---');
        const borrowId = readlineSync.question('شناسه امانت: ');
        
        const borrow = borrowManager.getBorrowById(parseInt(borrowId));
        if (!borrow) {
            console.log('امانت مورد نظر یافت نشد!');
            return;
        }

        if (borrow.status !== 'borrowed') {
            console.log('این امانت در حال حاضر فعال نیست!');
            return;
        }

        const returnDate = new Date().toISOString().split('T')[0];
        const isDelayed = new Date(returnDate) > new Date(borrow.endDate);
        
        borrowManager.returnBook(parseInt(borrowId), returnDate, isDelayed);
        console.log('کتاب با موفقیت دریافت شد!');
        
        if (isDelayed) {
            console.log('⚠️ این کتاب با تاخیر تحویل داده شده است!');
        }
    }

    getEmployeeById(id) {
        return this.employees.find(e => e.id === id);
    }

    getAllEmployees() {
        return this.employees;
    }
}

module.exports = EmployeeManager;
