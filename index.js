const readlineSync = require('readline-sync');
const StudentManager = require('./modules/StudentManager');
const BookManager = require('./modules/BookManager');
const EmployeeManager = require('./modules/EmployeeManager');
const AdminManager = require('./modules/AdminManager');
const GuestManager = require('./modules/GuestManager');
const BorrowManager = require('./modules/BorrowManager');

class LibraryManagementSystem {
    constructor() {
        this.studentManager = new StudentManager();
        this.bookManager = new BookManager();
        this.employeeManager = new EmployeeManager();
        this.adminManager = new AdminManager();
        this.guestManager = new GuestManager();
        this.borrowManager = new BorrowManager();
        this.currentUser = null;
        this.currentUserType = null;
        
        // Set up manager references
        this.borrowManager.setManagers(this.studentManager, this.bookManager);
    }

    start() {
        console.log('=== سیستم مدیریت کتابخانه دانشگاه ===\n');
        this.showMainMenu();
    }

    showMainMenu() {
        while (true) {
            console.log('\n--- منوی اصلی ---');
            console.log('1. ورود به عنوان مهمان');
            console.log('2. ثبت نام دانشجو');
            console.log('3. ورود به عنوان دانشجو');
            console.log('4. ورود به عنوان کارمند');
            console.log('5. ورود به عنوان مدیر');
            console.log('6. خروج');

            const choice = readlineSync.question('انتخاب خود را وارد کنید: ');

            switch (choice) {
                case '1':
                    this.guestMenu();
                    break;
                case '2':
                    this.studentRegistration();
                    break;
                case '3':
                    this.studentLogin();
                    break;
                case '4':
                    this.employeeLogin();
                    break;
                case '5':
                    this.adminLogin();
                    break;
                case '6':
                    console.log('خداحافظ!');
                    process.exit(0);
                default:
                    console.log('انتخاب نامعتبر! لطفاً دوباره تلاش کنید.');
            }
        }
    }

    guestMenu() {
        while (true) {
            console.log('\n--- منوی مهمان ---');
            console.log('1. مشاهده تعداد دانشجویان ثبت نام کرده');
            console.log('2. جستجوی کتاب بر اساس نام');
            console.log('3. مشاهده اطلاعات آماری');
            console.log('4. بازگشت به منوی اصلی');

            const choice = readlineSync.question('انتخاب خود را وارد کنید: ');

            switch (choice) {
                case '1':
                    this.guestManager.showRegisteredStudentsCount(this.studentManager);
                    break;
                case '2':
                    this.guestManager.searchBooks(this.bookManager);
                    break;
                case '3':
                    this.guestManager.showStatistics(this.studentManager, this.bookManager, this.borrowManager);
                    break;
                case '4':
                    return;
                default:
                    console.log('انتخاب نامعتبر! لطفاً دوباره تلاش کنید.');
            }
        }
    }

    studentRegistration() {
        const student = this.studentManager.register();
        if (student) {
            console.log('ثبت نام با موفقیت انجام شد! حالا می‌توانید وارد شوید.');
        }
    }

    studentLogin() {
        console.log('\n--- ورود دانشجو ---');
        const username = readlineSync.question('نام کاربری: ');
        const password = readlineSync.question('رمز عبور: ', { hideEchoBack: true });

        const student = this.studentManager.login(username, password);
        if (student) {
            this.currentUser = student;
            this.currentUserType = 'student';
            this.studentMenu();
        } else {
            console.log('نام کاربری یا رمز عبور اشتباه است!');
        }
    }

    studentMenu() {
        while (true) {
            console.log(`\n--- منوی دانشجو (${this.currentUser.username}) ---`);
            console.log('1. جستجوی کتاب');
            console.log('2. ثبت درخواست امانت');
            console.log('3. مشاهده درخواست‌های امانت');
            console.log('4. خروج');

            const choice = readlineSync.question('انتخاب خود را وارد کنید: ');

            switch (choice) {
                case '1':
                    this.studentManager.searchBooks(this.bookManager);
                    break;
                case '2':
                    this.studentManager.requestBorrow(this.bookManager, this.borrowManager);
                    break;
                case '3':
                    this.studentManager.showBorrowRequests(this.borrowManager);
                    break;
                case '4':
                    this.currentUser = null;
                    this.currentUserType = null;
                    return;
                default:
                    console.log('انتخاب نامعتبر! لطفاً دوباره تلاش کنید.');
            }
        }
    }

    employeeLogin() {
        console.log('\n--- ورود کارمند ---');
        const username = readlineSync.question('نام کاربری: ');
        const password = readlineSync.question('رمز عبور: ', { hideEchoBack: true });

        const employee = this.employeeManager.login(username, password);
        if (employee) {
            this.currentUser = employee;
            this.currentUserType = 'employee';
            this.employeeMenu();
        } else {
            console.log('نام کاربری یا رمز عبور اشتباه است!');
        }
    }

    employeeMenu() {
        while (true) {
            console.log(`\n--- منوی کارمند (${this.currentUser.username}) ---`);
            console.log('1. تغییر رمز عبور');
            console.log('2. ثبت اطلاعات کتاب');
            console.log('3. جستجو و ویرایش کتاب');
            console.log('4. بررسی درخواست‌های امانت');
            console.log('5. مشاهده گزارش دانشجو');
            console.log('6. فعال/غیرفعال کردن دانشجو');
            console.log('7. دریافت کتاب');
            console.log('8. خروج');

            const choice = readlineSync.question('انتخاب خود را وارد کنید: ');

            switch (choice) {
                case '1':
                    this.employeeManager.changePassword(this.currentUser);
                    break;
                case '2':
                    this.employeeManager.addBook(this.bookManager, this.currentUser);
                    break;
                case '3':
                    this.employeeManager.searchAndEditBook(this.bookManager);
                    break;
                case '4':
                    this.employeeManager.reviewBorrowRequests(this.borrowManager);
                    break;
                case '5':
                    this.employeeManager.showStudentReport(this.studentManager, this.borrowManager);
                    break;
                case '6':
                    this.employeeManager.toggleStudentStatus(this.studentManager);
                    break;
                case '7':
                    this.employeeManager.returnBook(this.borrowManager);
                    break;
                case '8':
                    this.currentUser = null;
                    this.currentUserType = null;
                    return;
                default:
                    console.log('انتخاب نامعتبر! لطفاً دوباره تلاش کنید.');
            }
        }
    }

    adminLogin() {
        console.log('\n--- ورود مدیر ---');
        const username = readlineSync.question('نام کاربری: ');
        const password = readlineSync.question('رمز عبور: ', { hideEchoBack: true });

        const admin = this.adminManager.login(username, password);
        if (admin) {
            this.currentUser = admin;
            this.currentUserType = 'admin';
            this.adminMenu();
        } else {
            console.log('نام کاربری یا رمز عبور اشتباه است!');
        }
    }

    adminMenu() {
        while (true) {
            console.log(`\n--- منوی مدیر (${this.currentUser.username}) ---`);
            console.log('1. تعریف کارمند');
            console.log('2. مشاهده عملکرد کارمند');
            console.log('3. مشاهده آمار امانات');
            console.log('4. مشاهده آمار دانشجویان');
            console.log('5. خروج');

            const choice = readlineSync.question('انتخاب خود را وارد کنید: ');

            switch (choice) {
                case '1':
                    this.adminManager.addEmployee(this.employeeManager);
                    break;
                case '2':
                    this.adminManager.showEmployeePerformance(this.employeeManager, this.bookManager, this.borrowManager);
                    break;
                case '3':
                    this.adminManager.showBorrowStatistics(this.borrowManager);
                    break;
                case '4':
                    this.adminManager.showStudentStatistics(this.studentManager, this.borrowManager);
                    break;
                case '5':
                    this.currentUser = null;
                    this.currentUserType = null;
                    return;
                default:
                    console.log('انتخاب نامعتبر! لطفاً دوباره تلاش کنید.');
            }
        }
    }
}

// Start the application
const app = new LibraryManagementSystem();
app.start();
