const readlineSync = require('readline-sync');

class AdminManager {
    constructor() {
        this.admins = [
            {
                id: 1,
                username: 'admin',
                password: 'admin123'
            }
        ];
    }

    login(username, password) {
        const admin = this.admins.find(a => a.username === username && a.password === password);
        return admin || null;
    }

    addEmployee(employeeManager) {
        console.log('\n--- تعریف کارمند جدید ---');
        const username = readlineSync.question('نام کاربری: ');
        const password = readlineSync.question('رمز عبور: ', { hideEchoBack: true });
        const confirmPassword = readlineSync.question('تکرار رمز عبور: ', { hideEchoBack: true });

        if (password !== confirmPassword) {
            console.log('رمزهای عبور مطابقت ندارند!');
            return false;
        }

        const employee = employeeManager.addEmployee(username, password);
        return employee !== null;
    }

    showEmployeePerformance(employeeManager, bookManager, borrowManager) {
        console.log('\n--- عملکرد کارمندان ---');
        const employees = employeeManager.getAllEmployees();

        if (employees.length === 0) {
            console.log('هیچ کارمندی ثبت نشده است.');
            return;
        }

        employees.forEach(employee => {
            const booksAdded = bookManager.getBooksAddedByEmployee(employee.id).length;
            const booksBorrowed = borrowManager.getBooksBorrowedByEmployee(employee.id).length;
            const booksReturned = borrowManager.getBooksReturnedByEmployee(employee.id).length;

            console.log(`\nکارمند: ${employee.username}`);
            console.log(`تاریخ اضافه شدن: ${employee.addedDate.toLocaleDateString('fa-IR')}`);
            console.log(`تعداد کتاب‌های ثبت شده: ${booksAdded}`);
            console.log(`تعداد کتاب‌های امانت داده: ${booksBorrowed}`);
            console.log(`تعداد کتاب‌های تحویل گرفته: ${booksReturned}`);
        });
    }

    showBorrowStatistics(borrowManager) {
        console.log('\n--- آمار امانات ---');
        const allBorrows = borrowManager.getAllBorrows();
        
        const totalRequests = allBorrows.length;
        const totalBorrowed = allBorrows.filter(b => b.status === 'borrowed' || b.status === 'returned').length;
        
        const returnedBorrows = allBorrows.filter(b => b.status === 'returned');
        let averageDays = 0;
        
        if (returnedBorrows.length > 0) {
            const totalDays = returnedBorrows.reduce((sum, borrow) => {
                const startDate = new Date(borrow.startDate);
                const returnDate = new Date(borrow.returnDate);
                return sum + Math.ceil((returnDate - startDate) / (1000 * 60 * 60 * 24));
            }, 0);
            averageDays = Math.round(totalDays / returnedBorrows.length);
        }

        console.log(`تعداد کل درخواست‌های امانت: ${totalRequests}`);
        console.log(`تعداد کل امانت‌های انجام شده: ${totalBorrowed}`);
        console.log(`میانگین تعداد روزهای امانت: ${averageDays} روز`);
        
        const currentBorrows = allBorrows.filter(b => b.status === 'borrowed').length;
        console.log(`تعداد کتاب‌های در حال امانت: ${currentBorrows}`);
    }

    showStudentStatistics(studentManager, borrowManager) {
        console.log('\n--- آمار دانشجویان ---');
        const allStudents = studentManager.getAllStudents();
        const activeStudents = studentManager.getActiveStudents();
        
        console.log(`تعداد کل دانشجویان: ${allStudents.length}`);
        console.log(`تعداد دانشجویان فعال: ${activeStudents.length}`);
        console.log(`تعداد دانشجویان غیرفعال: ${allStudents.length - activeStudents.length}`);

        // Top 10 students with most delays
        const studentDelays = [];
        allStudents.forEach(student => {
            const history = borrowManager.getStudentHistory(student.id);
            const delays = history.filter(b => b.isDelayed).length;
            if (delays > 0) {
                studentDelays.push({
                    username: student.username,
                    delays: delays
                });
            }
        });

        studentDelays.sort((a, b) => b.delays - a.delays);
        
        console.log('\n10 دانشجوی با بیشترین تاخیر:');
        if (studentDelays.length === 0) {
            console.log('هیچ دانشجویی تاخیر نداشته است.');
        } else {
            studentDelays.slice(0, 10).forEach((student, index) => {
                console.log(`${index + 1}. ${student.username}: ${student.delays} تاخیر`);
            });
        }

        // Show detailed statistics for each student
        console.log('\nآمار تفصیلی دانشجویان:');
        allStudents.forEach(student => {
            const history = borrowManager.getStudentHistory(student.id);
            const notReturned = history.filter(b => b.status === 'borrowed').length;
            const delayed = history.filter(b => b.isDelayed).length;

            console.log(`\n${student.username}:`);
            console.log(`  کل امانات: ${history.length}`);
            console.log(`  کتاب‌های تحویل داده نشده: ${notReturned}`);
            console.log(`  امانت‌های با تاخیر: ${delayed}`);
        });
    }
}

module.exports = AdminManager;
