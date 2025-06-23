export default {
  sidebar: {
    title: 'Library System',
    navigation: 'Navigation',
    libraryManagement: 'Library Management',
    administration: 'Administration',
    language: 'Language',
    home: 'Home',
    dashboard: 'Dashboard',
    books: 'Books',
    notices: 'Notices',
    favorites: 'Favorites',
    billingCenter: 'Billing Center',
    myBorrowing: 'My Borrowing',
    adminDashboard: 'Admin Dashboard',
    manageBooks: 'Manage Books',
    userManagement: 'User Management',
    borrowingManagement: 'Borrowing Management',
    borrowingRules: 'Borrowing Rules',
    feeManagement: 'Fee Management',
    reports: 'Reports',
    profile: 'Profile',
    logout: 'Logout',
    anonymous: 'Anonymous',
    viewProfile: 'View Profile',
    clickToLogin: 'Click here to login',
  },
  home: {
    hero: {
      welcomeBack: 'Welcome back, {username}!',
      welcomeBackDefault: 'Welcome back, Reader!',
      welcomeToLibrary: 'Welcome to Your Digital Library',
      subtitleAuthenticated: 'Discover new worlds, continue your reading journey, and explore our vast collection.',
      subtitleGuest: 'Discover thousands of books, manage your reading journey, and connect with a community of readers.',
      searchPlaceholder: 'Search books, ISBN or author...',
      searchButton: 'Search'
    },
    quickActions: {
      title: 'Quick Actions',
      browseBooks: {
        title: 'Browse Books',
        description: 'Explore our collection'
      },
      myBorrows: {
        title: 'My Borrows',
        description: 'View borrowed books'
      },
      dashboard: {
        title: 'Dashboard',
        description: 'Your reading stats'
      },
      signIn: {
        title: 'Sign In',
        description: 'Access your account'
      },
      register: {
        title: 'Register',
        description: 'Join our community'
      }
    },
    categories: {
      title: 'Browse by Category'
    },
    recommendedBooks: {
      title: 'Recommended Books',
      subtitle: 'Recently added to our collection',
      viewAll: 'View All'
    },
    popularBooks: {
      title: 'Hot Borrows',
      subtitle: 'Most borrowed books recently'
    },
    libraryFeatures: {
      title: 'Why Choose Our Library?',
      subtitle: 'Discover the benefits of joining our digital library community',
      vastCollection: {
        title: 'Vast Collection',
        description: 'Access thousands of books across all genres and categories.'
      },
      community: {
        title: 'Community',
        description: 'Connect with fellow readers and share your reading experience.'
      },
      easyManagement: {
        title: 'Easy Management',
        description: 'Track your borrowed books and manage your reading schedule.'
      },
      personalized: {
        title: 'Personalized',
        description: 'Get recommendations based on your reading preferences.'
      }
    },
    status: {
      available: 'Available',
      outOfStock: 'Out of Stock',
      unknownAuthor: 'Unknown author',
      available_count: 'available',
      borrows: 'borrows',
    },
    loading: {
      content: 'Loading library content...',
      error: 'Failed to load some content'
    }
  },
  "routes": {
    "home": "Home",
    "dashboard": "Dashboard",
    "login": "Login",
    "register": "Register",
    "profile": "Profile",
    "notices": "Notices",
    "reservations": "Reservations",
    "favorites": "Favorites", 
    "billing": "Billing",
    "api-test": "API Test",
    "not-found": "Page Not Found",
    "books": "Books",
    "book-detail": "Book Details",
    "book": {
      "index": "Books",
      "detail": "Book Details",
      "copies": "Book Copies",
      "create": "Create Book",
      "edit": "Edit Book"
    },
    "borrows": "My Borrowing",
    "admin": {
      "dashboard": "Admin Dashboard",
      "books": "Manage Books",
      "book-create": "Create Book",
      "book-edit": "Edit Book", 
      "users": "User Management",
      "borrowing": "Borrowing Management",
      "borrowing-rules": "Borrowing Rules",
      "fees": "Fee Management",
      "reports": "Reports"
    }
  },
  books: {
    title: 'Books',
    description: 'Browse and manage library books',
    addBook: 'Add Book',
    search: {
      label: 'Search',
      placeholder: 'Search books by title, author, or ISBN...',
      button: 'Search'
    },
    filters: {
      category: {
        label: 'Category',
        all: 'All Categories'
      },
      language: {
        label: 'Language',
        all: 'All Languages'
      },
      sort: {
        label: 'Sort By',
        titleAsc: 'Title (A-Z)',
        titleDesc: 'Title (Z-A)',
        availableFirst: 'Available First',
        totalCopies: 'Total Copies'
      }
    },
    results: {
      showing: 'Showing {count} of {total} books',
      loading: 'Loading books...',
      noResults: 'No books found matching your criteria.'
    },
    pagination: {
      previous: 'Previous',
      next: 'Next',
      page: 'Page {current} of {total}'
    },
    form: {
      title: 'Add Book',
      description: 'Fill in the details of the new book',
      fields: {
        title: {
          label: 'Title',
          placeholder: 'Enter book title...'
        },
        isbn: {
          label: 'ISBN',
          placeholder: 'Enter ISBN...'
        },
        language: {
          label: 'Language',
          placeholder: 'Enter language...'
        },
        category: {
          label: 'Category',
          placeholder: 'Enter category...'
        },
        authors: {
          label: 'Authors',
          placeholder: 'Enter authors (comma-separated)...'
        },
        publisher: {
          label: 'Publisher',
          placeholder: 'Enter publisher...'
        },
        publishedYear: {
          label: 'Published Year'
        },
        description: {
          label: 'Description',
          placeholder: 'Enter book description...'
        },
        coverURL: {
          label: 'Cover Image URL',
          placeholder: 'https://example.com/book-cover.jpg (optional)'
        },
        totalQuantity: {
          label: 'Total Quantity'
        }
      },
      buttons: {
        cancel: 'Cancel',
        add: 'Add Book',
        adding: 'Adding...'
      },
      validation: {
        requiredFields: 'Please fill in all required fields'
      },
      messages: {
        success: 'Book added successfully!',
        error: 'Failed to add book'
      }
    },
    errors: {
      loadFailed: 'Failed to load books'
    }
  },
  bookCard: {
    loadingImage: 'Loading image...',
    noImageAvailable: 'No image available',
    status: {
      available: 'Available',
      limited: 'Limited',
      outOfStock: 'Out of Stock',
      notAvailable: 'Not Available'
    },
    availability: {
      singleCopy: '1 copy available',
      multipleCopies: '{count} copies available',
      total: 'total'
    },
    authorPrefix: 'By: ',
    isbnPrefix: 'ISBN: '
  },
  notices: {
    title: 'Library Notices',
    description: 'Stay updated with library announcements and news',
    addNotice: 'Add Notice',
    search: {
      placeholder: 'Search notices...',
      showing: '{count} of {total} notices'
    },
    loading: 'Loading notices...',
    empty: {
      title: 'No Notices Yet',
      description: 'There are no notices to display at the moment.',
      createFirst: 'Create First Notice'
    },
    noResults: 'No notices found matching your search.',
    status: {
      new: 'New',
      pinned: 'Pinned',
      show: 'Show'
    },
    time: {
      justNow: 'Just now',
      minutesAgo: '{count} minute{plural} ago',
      hoursAgo: '{count} hour{plural} ago',
      daysAgo: '{count} day{plural} ago',
      updated: 'Updated {time}'
    },
    form: {
      create: {
        title: 'Create New Notice',
        description: 'Create a new announcement for library users.'
      },
      edit: {
        title: 'Edit Notice',
        description: 'Update the notice information.'
      },
      fields: {
        title: {
          label: 'Title',
          placeholder: 'Enter notice title...'
        },
        content: {
          label: 'Content',
          placeholder: 'Enter notice content...'
        },
        publishTime: {
          label: 'Publish Time',
          required: 'Publish Time *'
        },
        expireTime: {
          label: 'Expire Time (optional)'
        },
        status: {
          label: 'Status',
          required: 'Status *',
          placeholder: 'Select status'
        }
      },
      buttons: {
        cancel: 'Cancel',
        create: 'Create Notice',
        creating: 'Creating...',
        update: 'Update Notice',
        updating: 'Updating...'
      },
      validation: {
        requiredFields: 'Please fill in all required fields'
      }
    },
    delete: {
      title: 'Delete Notice',
      description: 'Are you sure you want to delete this notice? This action cannot be undone.',
      confirm: 'Delete',
      cancel: 'Cancel'
    },
    pagination: {
      previous: 'Previous',
      next: 'Next',
      page: 'Page {current} of {total}'
    },
    messages: {
      createSuccess: 'Notice created successfully!',
      createError: 'Failed to create notice',
      updateSuccess: 'Notice updated successfully!',
      updateError: 'Failed to update notice',
      deleteSuccess: 'Notice deleted successfully!',
      deleteError: 'Failed to delete notice',
      loadError: 'Failed to load notices'
    }
  },
  favorites: {
    title: 'Favorites',
    description: 'Manage your favorite books',
    loading: 'Loading favorites...',
    pageTitle: 'My Favorite Books',
    count: {
      singular: '{count} book in your favorites',
      plural: '{count} books in your favorites'
    },
    empty: {
      title: 'No favorites yet',
      description: 'Start adding books to your favorites to see them here.'
    },
    table: {
      headers: {
        title: 'Book Title',
        authors: 'Author(s)',
        category: 'Category',
        addedDate: 'Added Date',
        actions: 'Actions'
      },
      actions: {
        viewDetails: 'View Details',
        remove: 'Remove',
        removing: 'Removing...'
      },
      categoryDefault: 'Uncategorized',
      isbnPrefix: 'ISBN: '
    },
    dialogs: {
      removeConfirm: 'Are you sure you want to remove this book from your favorites?'
    },
    messages: {
      removeSuccess: 'Book removed from favorites!',
      removeError: 'Failed to remove favorite. Please try again.',
      loadError: 'Failed to load favorites'
    }
  },
  billing: {
    title: 'Billing Center',
    description: 'Manage your fees and payments',
    loading: 'Loading billing information...',
    sections: {
      overdueFees: {
        title: 'Overdue Fees',
        description: 'Fees for late book returns'
      },
      paymentSummary: {
        title: 'Payment Summary'
      }
    },
    table: {
      headers: {
        borrowRecordId: 'Borrow Record ID',
        feeAmount: 'Fee Amount',
        status: 'Status',
        actions: 'Actions'
      }
    },
    status: {
      paid: 'Paid',
      unpaid: 'Unpaid'
    },
    actions: {
      payFee: 'Pay Fee',
      processing: 'Processing...'
    },
    summary: {
      unpaidFees: 'Unpaid Fees',
      totalUnpaidAmount: 'Total Unpaid Amount',
      paidFees: 'Paid Fees'
    },
    empty: {
      noOverdueFees: 'No overdue fees found'
    },
    dialogs: {
      payConfirm: 'Are you sure you want to pay this fee?'
    },
    messages: {
      loadError: 'Failed to load billing information',
      paySuccess: 'Fee paid successfully!',
      payError: 'Failed to pay fee. Please try again.'
    }
  },
  borrowing: {
    title: 'My Borrowing',
    description: 'Manage your borrowed books and fees',
    loading: 'Loading your borrowing history...',
    buttons: {
      browseBooks: 'Browse Books',
      refresh: 'Refresh',
      renew: 'Renew',
      return: 'Return',
      returning: 'Returning...',
      payNow: 'Pay Now'
    },
    summary: {
      activeBorrows: {
        title: 'Active Borrows',
        description: 'Currently borrowed'
      },
      overdueBooks: {
        title: 'Overdue Books',
        description: 'Need immediate attention'
      },
      outstandingFees: {
        title: 'Outstanding Fees',
        description: 'Unpaid late fees'
      }
    },
    filters: {
      search: {
        label: 'Search Books',
        placeholder: 'Search by title, author, or ISBN...'
      },
      status: {
        label: 'Status',
        all: 'All Borrows',
        active: 'Active',
        returned: 'Returned',
        overdue: 'Overdue'
      }
    },
    status: {
      returned: 'Returned',
      overdue: 'Overdue',
      dueSoon: 'Due Soon',
      active: 'Active'
    },
    fees: {
      title: 'Outstanding Fees',
      description: 'Please pay your late fees to continue borrowing',
      borrowId: 'Borrow ID: {id}',
      feeDate: 'Fee Date: {date}'
    },
    history: {
      title: 'Borrowing History',
      showing: 'Showing {count} of {total} borrows',
      isbn: 'ISBN: {isbn}',
      borrowed: 'Borrowed: {date}',
      due: 'Due: {date}',
      returned: 'Returned: {date}',
      daysLeft: '{days} days left',
      daysOverdue: '{days} days overdue'
    },
    empty: {
      noBorrows: 'No borrows found matching your criteria',
      noFees: 'No outstanding fees'
    },
    pagination: {
      previous: 'Previous',
      next: 'Next',
      page: 'Page {current} of {total}'
    },
    messages: {
      loadError: 'Failed to load borrowing history',
      returnSuccess: 'Book returned successfully!',
      returnError: 'Failed to return book',
      renewSuccess: 'Book renewed successfully!',
      renewError: 'Failed to renew book',
      paySuccess: 'Fee paid successfully!',
      payError: 'Failed to pay fee'
    }
  },
  bookCopies: {
    title: 'Book Copies',
    breadcrumb: {
      books: 'Books',
      copies: 'Copies'
    },
    header: {
      title: 'Book Copies',
      addCopy: 'Add Copy'
    },
    loading: {
      bookDetails: 'Loading book details...',
      error: 'Error Loading Book',
      tryAgain: 'Try Again'
    },
    dialogs: {
      create: {
        title: 'Add New Copy',
        description: 'Create a new copy of "{title}"',
        fields: {
          barcode: {
            label: 'Barcode',
            placeholder: 'Enter barcode...'
          },
          status: {
            label: 'Status',
            placeholder: 'Select status'
          },
          purchasePrice: {
            label: 'Purchase Price (Optional)',
            placeholder: '0.00'
          }
        },
        buttons: {
          cancel: 'Cancel',
          create: 'Create Copy'
        }
      },
      edit: {
        title: 'Edit Copy',
        description: 'Update copy details',
        fields: {
          barcode: {
            label: 'Barcode',
            placeholder: 'Enter barcode...'
          },
          status: {
            label: 'Status',
            placeholder: 'Select status'
          },
          purchasePrice: {
            label: 'Purchase Price',
            placeholder: '0.00'
          }
        },
        buttons: {
          cancel: 'Cancel',
          update: 'Update Copy'
        }
      }
    },
    status: {
      available: 'Available',
      borrowed: 'Borrowed',
      maintenance: 'Maintenance',
      scrapped: 'Scrapped',
      discarded: 'Discarded'
    },
    messages: {
      createSuccess: 'Copy created successfully',
      createError: 'Failed to create copy',
      updateSuccess: 'Copy updated successfully',
      updateError: 'Failed to update copy',
      borrowSuccess: 'Book borrowed successfully',
      borrowError: 'Failed to borrow book',
      returnSuccess: 'Book returned successfully',
      returnError: 'Failed to return book',
      maintenanceSuccess: 'Copy status updated to maintenance',
      maintenanceError: 'Failed to update copy status',
      loginRequired: 'Please login to borrow books',
      loginRequiredReturn: 'Please login to return books',
      noBorrowRecord: 'No active borrow record found for this book copy'
    }
  },
  adminBorrowing: {
    title: 'Admin Borrowing Management',
    description: 'Register and manage book borrowing for users',
    buttons: {
      registerBorrow: 'Register Borrow',
      cancel: 'Cancel',
      creating: 'Creating...',
      refresh: 'Refresh'
    },
    summary: {
      totalBorrows: {
        title: 'Total Borrows',
        description: 'All borrowing records'
      },
      activeBorrows: {
        title: 'Active Borrows',
        description: 'Currently borrowed'
      },
      overdueBooks: {
        title: 'Overdue Books',
        description: 'Need attention'
      }
    },
    dialog: {
      title: 'Register Book Borrowing',
      description: 'Select a user and book copy to register a new borrowing record',
      userSelection: {
        label: 'Select User',
        placeholder: 'Search users by username...',
        idPrefix: 'ID: '
      },
      bookSelection: {
        label: 'Select Book',
        placeholder: 'Search books by title, author, or ISBN...',
        available: 'Available: {available} / {total}',
        isbnPrefix: 'ISBN: '
      },
      copySelection: {
        label: 'Select Available Copy',
        copyNumber: 'Copy #{id}',
        availableStatus: 'Available',
        noAvailableCopies: 'No available copies for this book'
      }
    },
    filters: {
      search: {
        label: 'Search Records',
        placeholder: 'Search by user, book title, or ISBN...'
      },
      status: {
        label: 'Status',
        allBorrows: 'All Borrows',
        active: 'Active',
        returned: 'Returned',
        overdue: 'Overdue'
      }
    },
    list: {
      title: 'All Borrowing Records',
      description: 'Showing {count} of {total} records',
      loading: 'Loading borrowing records...',
      empty: 'No borrowing records found',
      copyPrefix: 'Copy #',
      isbnPrefix: 'ISBN: ',
      borrowed: 'Borrowed: {date}',
      due: 'Due: {date}',
      returned: 'Returned: {date}',
      daysLeft: '{days} days left',
      daysOverdue: '{days} days overdue'
    },
    status: {
      returned: 'Returned',
      overdue: 'Overdue',
      dueSoon: 'Due Soon',
      active: 'Active'
    },
    messages: {
      success: 'Book borrowed successfully!',
      error: 'Failed to create borrowing record',
      loadBorrowsError: 'Failed to load borrowing records',
      loadUsersError: 'Failed to load users',
      loadBooksError: 'Failed to load books',
      loadCopiesError: 'Failed to load book copies',
      validationError: 'Please select both a user and a book copy'
    }
  },
  bookForm: {
    title: {
      create: 'Add New Book',
      edit: 'Edit Book'
    },
    description: {
      create: 'Add a new book to the library',
      edit: 'Update book information'
    },
    loading: {
      book: 'Loading book data...',
      saving: 'Saving...'
    },
    sections: {
      basicInfo: {
        title: 'Basic Information',
        description: 'Enter the book\'s basic details'
      },
      authors: {
        title: 'Authors',
        description: 'Add the book\'s authors'
      },
      publishers: {
        title: 'Publishers',
        description: 'Add the book\'s publishers'
      },
      category: {
        title: 'Category',
        description: 'Specify the book\'s category'
      }
    },
    fields: {
      title: {
        label: 'Title',
        placeholder: 'Enter book title',
        required: 'Title *'
      },
      isbn: {
        label: 'ISBN',
        placeholder: 'Enter ISBN',
        required: 'ISBN *'
      },
      language: {
        label: 'Language',
        placeholder: 'e.g., English, Spanish, etc.',
        required: 'Language *'
      },
      location: {
        label: 'Location',
        placeholder: 'e.g., LIBRARY, STORAGE, etc.'
      },
      totalQuantity: {
        label: 'Total Quantity',
        required: 'Total Quantity *'
      },
      description: {
        label: 'Description',
        placeholder: 'Enter book description (optional)'
      },
      coverURL: {
        label: 'Cover Image URL',
        placeholder: 'https://example.com/book-cover.jpg (optional)'
      },
      category: {
        label: 'Category',
        placeholder: 'Enter category name',
        required: 'Category *'
      },
      newAuthor: {
        placeholder: 'Enter author name'
      },
      newPublisher: {
        placeholder: 'Enter publisher name'
      }
    },
    buttons: {
      back: 'Back',
      cancel: 'Cancel',
      save: 'Save',
      create: 'Create Book',
      update: 'Update Book',
      creating: 'Creating...',
      updating: 'Updating...',
      add: 'Add',
      remove: 'Remove'
    },
    validation: {
      titleRequired: 'Title is required',
      isbnRequired: 'ISBN is required',
      languageRequired: 'Language is required',
      authorsRequired: 'At least one author is required',
      publishersRequired: 'At least one publisher is required',
      categoryRequired: 'Category is required',
      quantityMinimum: 'Total quantity must be at least 1'
    },
    messages: {
      loadError: 'Failed to load book data',
      saveError: 'Failed to save book',
      createSuccess: 'Book created successfully!',
      updateSuccess: 'Book updated successfully!'
    }
  },
  dashboard: {
    greeting: {
      morning: 'Good morning',
      afternoon: 'Good afternoon',
      evening: 'Good evening',
      subtitle: 'Ready to explore your library today?'
    },
    loading: 'Loading your dashboard...',
    user: {
      roles: {
        admin: 'Administrator',
        user: 'Reader'
      },
      memberSince: 'Member since',
      recently: 'recently'
    },
    stats: {
      availableBooks: {
        title: 'Available Books',
        description: 'Ready to borrow'
      },
      activeLoans: {
        title: 'Your Active Loans',
        description: 'Currently borrowed'
      },
      booksRead: {
        title: 'Books Read',
        description: 'Successfully returned'
      },
      overdueBooks: {
        title: 'Overdue Books',
        description: 'Need to return'
      }
    },
    quickActions: {
      title: 'Quick Actions',
      subtitle: 'What would you like to do today?',
      browseBooks: {
        title: 'Browse Books',
        description: 'Explore available books'
      },
      myBorrowings: {
        title: 'My Borrowings',
        description: 'View borrowing history'
      },
      myProfile: {
        title: 'My Profile',
        description: 'Manage your account'
      },
      readingList: {
        title: 'Reading List',
        description: 'Manage your wishlist'
      }
    },
    newArrivals: {
      title: 'New Arrivals',
      description: 'Recently added to the library',
      browseAll: 'Browse All',
      noBooks: 'No recent books found'
    },
    recentActivity: {
      title: 'Your Recent Activity',
      description: 'Your borrowing history',
      viewAll: 'View All',
      notAuthenticated: 'Please log in to view your activity',
      noActivity: 'No borrowing activity yet',
      startBrowsing: 'Start browsing books',
      returned: 'Returned',
      due: 'Due'
    },
    notices: {
      title: 'Library Notices',
      description: 'Important updates and announcements',
      viewAll: 'View All',
      noNotices: 'No recent notices'
    },
    recommendations: {
      title: 'Recommended for You',
      description: 'Based on your reading history and preferences',
      comingSoon: 'Personalized recommendations coming soon!',
      keepReading: 'Keep borrowing books to help us learn your preferences.'
    },
    messages: {
      loadError: 'Failed to load statistics'
    }
  },
  bookDetail: {
    title: 'Book Details',
    backToBooks: 'Back to Books',
    loading: 'Loading book details...',
    cover: {
      noImageAvailable: 'No cover image available',
      altText: 'Cover of {title}'
    },
    availability: {
      available: 'Available',
      limited: 'Limited',
      outOfStock: 'Out of Stock',
      notAvailable: 'Not Available',
      singleCopy: '1 copy available',
      multipleCopies: '{count} copies available',
      totalCopies: 'Total copies: {count}'
    },
    rating: {
      singleReview: '1 review',
      multipleReviews: '{count} reviews'
    },
    buttons: {
      borrow: 'Borrow Book',
      borrowing: 'Borrowing...',
      viewCopies: 'View Copies',
      share: 'Share',
      edit: 'Edit Book'
    },
    details: {
      title: 'Book Details',
      isbn: 'ISBN:',
      category: 'Category:',
      publishers: 'Publishers:',
      language: 'Language:',
      available: 'Available:',
      uncategorized: 'Uncategorized'
    },
    description: {
      title: 'Description'
    },
    comments: {
      title: 'Reviews & Comments',
      description: 'See what others think about this book'
    },
    notFound: {
      title: 'Book Not Found',
      description: 'The book you\'re looking for doesn\'t exist.',
      backButton: 'Back to Books'
    },
    messages: {
      loadError: 'Failed to load book details',
      borrowSuccess: 'Book borrowed successfully!',
      borrowError: 'Failed to borrow book',
      borrowLoginRequired: 'Please log in to borrow books',
      borrowNotAvailable: 'This book is not available for borrowing',
      shareSuccess: 'Link copied to clipboard!'
    }
  },
  adminDashboard: {
    title: 'Admin Dashboard',
    subtitle: 'System overview and administrative controls',
    administrator: 'Administrator',
    loading: 'Loading admin dashboard...',
    stats: {
      totalUsers: {
        title: 'Total Users',
        description: 'Registered users'
      },
      activeLoans: {
        title: 'Active Loans',
        description: 'Currently borrowed'
      },
      overdueBooks: {
        title: 'Overdue Books',
        description: 'Need attention'
      },
      totalBooks: {
        title: 'Total Books',
        description: 'In library'
      }
    },
    actions: {
      title: 'Administrative Actions',
      description: 'Common administrative tasks and system management',
      addNewBook: {
        title: 'Add New Book',
        description: 'Add books to library'
      },
      manageUsers: {
        title: 'Manage Users',
        description: 'User administration'
      },
      borrowingRules: {
        title: 'Borrowing Rules',
        description: 'Configure borrowing'
      },
      feeManagement: {
        title: 'Fee Management',
        description: 'Manage fees & fines'
      },
      systemReports: {
        title: 'System Reports',
        description: 'Generate reports'
      },
      manageNotices: {
        title: 'Manage Notices',
        description: 'System announcements'
      }
    },
    sections: {
      recentBooks: {
        title: 'Recently Added Books',
        description: 'Latest additions to the library',
        manageAll: 'Manage All',
        noBooks: 'No recent books found',
        unknownAuthor: 'Unknown author'
      },
      overdueBorrows: {
        title: 'Overdue Borrowings',
        description: 'Books that need admin attention',
        manageAll: 'Manage All',
        noOverdue: 'No overdue borrowings',
        userPrefix: 'User: ',
        duePrefix: 'Due: ',
        unknownUser: 'Unknown user',
        unknownBook: 'Unknown book'
      },
      systemNotices: {
        title: 'System Notices',
        description: 'Manage announcements and system messages',
        createNotice: 'Create Notice',
        manageAll: 'Manage All',
        noNotices: 'No recent notices',
        untitled: 'Untitled',
        noContent: 'No content',
        publishedPrefix: 'Published: ',
        edit: 'Edit'
      },
      bookInventory: {
        title: 'Book Inventory by Category',
        description: 'Book availability across different categories',
        manageInventory: 'Manage Inventory',
        noData: 'No inventory data available',
        totalPrefix: 'Total: ',
        availablePrefix: 'Available: ',
        borrowedPrefix: 'Borrowed: ',
        availableSuffix: '% Available'
      }
    },
    status: {
      returned: 'Returned',
      overdue: 'Overdue',
      dueSoon: 'Due Soon',
      active: 'Active'
    },
    messages: {
      loadStatisticsError: 'Failed to load statistics',
      loadSystemStatsError: 'Failed to load some system statistics',
      loadRecentBooksError: 'Failed to load recent books',
      loadOverdueBorrowsError: 'Failed to load overdue borrows',
      loadRecentNoticesError: 'Failed to load recent notices',
      loadInventoryStatsError: 'Failed to load inventory statistics'
    }
  }
}
