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
  }
}
