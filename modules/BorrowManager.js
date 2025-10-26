const readlineSync = require('readline-sync');

class BorrowManager {
    constructor() {
        this.borrows = [];
        this.nextId = 1;
    }

    createBorrowRequest(studentId, bookId, startDate, endDate) {
        const request = {
            id: this.nextId++,
            studentId: studentId,
            bookId: bookId,
            startDate: startDate,
            endDate: endDate,
            status: 'pending', // pending, approved, borrowed, returned, rejected
            requestDate: new Date().toISOString().split('T')[0],
            approvedBy: null,
            approvedDate: null,
            returnDate: null,
            isDelayed: false
        };

        this.borrows.push(request);
        return request;
    }

    approveRequest(borrowId, employeeId) {
        const borrow = this.borrows.find(b => b.id === borrowId);
        if (borrow && borrow.status === 'pending') {
            borrow.status = 'approved';
            borrow.approvedBy = employeeId;
            borrow.approvedDate = new Date().toISOString().split('T')[0];
            return true;
        }
        return false;
    }

    rejectRequest(borrowId) {
        const borrow = this.borrows.find(b => b.id === borrowId);
        if (borrow && borrow.status === 'pending') {
            borrow.status = 'rejected';
            return true;
        }
        return false;
    }

    borrowBook(borrowId) {
        const borrow = this.borrows.find(b => b.id === borrowId);
        if (borrow && borrow.status === 'approved') {
            borrow.status = 'borrowed';
            return true;
        }
        return false;
    }

    returnBook(borrowId, returnDate, isDelayed = false) {
        const borrow = this.borrows.find(b => b.id === borrowId);
        if (borrow && borrow.status === 'borrowed') {
            borrow.status = 'returned';
            borrow.returnDate = returnDate;
            borrow.isDelayed = isDelayed;
            return true;
        }
        return false;
    }

    getStudentRequests(studentId) {
        return this.borrows
            .filter(b => b.studentId === studentId)
            .map(borrow => ({
                ...borrow,
                bookTitle: this.getBookTitle(borrow.bookId),
                studentUsername: this.getStudentUsername(borrow.studentId)
            }));
    }

    getStudentHistory(studentId) {
        return this.borrows
            .filter(b => b.studentId === studentId)
            .map(borrow => ({
                ...borrow,
                bookTitle: this.getBookTitle(borrow.bookId),
                studentUsername: this.getStudentUsername(borrow.studentId)
            }));
    }

    getPendingRequests() {
        return this.borrows
            .filter(b => b.status === 'pending')
            .map(borrow => ({
                ...borrow,
                bookTitle: this.getBookTitle(borrow.bookId),
                studentUsername: this.getStudentUsername(borrow.studentId)
            }));
    }

    getBorrowById(borrowId) {
        return this.borrows.find(b => b.id === borrowId);
    }

    getAllBorrows() {
        return this.borrows.map(borrow => ({
            ...borrow,
            bookTitle: this.getBookTitle(borrow.bookId),
            studentUsername: this.getStudentUsername(borrow.studentId)
        }));
    }

    getBooksBorrowedByEmployee(employeeId) {
        return this.borrows.filter(b => b.approvedBy === employeeId && b.status === 'borrowed');
    }

    getBooksReturnedByEmployee(employeeId) {
        return this.borrows.filter(b => b.approvedBy === employeeId && b.status === 'returned');
    }

    // Helper methods - these would normally get data from other managers
    getBookTitle(bookId) {
        // This is a placeholder - in a real system, this would query the BookManager
        return `کتاب ${bookId}`;
    }

    getStudentUsername(studentId) {
        // This is a placeholder - in a real system, this would query the StudentManager
        return `دانشجو ${studentId}`;
    }

    // Method to set references to other managers for proper data access
    setManagers(studentManager, bookManager) {
        this.studentManager = studentManager;
        this.bookManager = bookManager;
        
        // Override helper methods with actual data
        this.getBookTitle = (bookId) => {
            const book = this.bookManager.getBookById(bookId);
            return book ? book.title : `کتاب ${bookId}`;
        };
        
        this.getStudentUsername = (studentId) => {
            const student = this.studentManager.getStudentById(studentId);
            return student ? student.username : `دانشجو ${studentId}`;
        };
    }
}

module.exports = BorrowManager;
