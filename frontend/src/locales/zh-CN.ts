export default {
  sidebar: {
    title: '图书管理系统',
    navigation: '导航',
    libraryManagement: '图书管理',
    administration: '系统管理',
    language: '语言',
    home: '首页',
    dashboard: '面板',
    books: '图书',
    notices: '公告',
    favorites: '收藏',
    billingCenter: '缴费中心',
    myBorrowing: '我的借阅',
    adminDashboard: '管理员面板',
    manageBooks: '图书管理',
    userManagement: '用户管理',
    borrowingManagement: '借阅管理',
    borrowingRules: '借阅规则',
    feeManagement: '费用管理',
    reports: '报表',
    profile: '个人资料',
    logout: '退出登录',
    anonymous: '游客',
    viewProfile: '查看个人资料',
    clickToLogin: '点击这里登录',
  },
  home: {
    hero: {
      welcomeBack: '欢迎回来，{username}！',
      welcomeBackDefault: '欢迎回来，读者！',
      welcomeToLibrary: '欢迎来到您的数字图书馆',
      subtitleAuthenticated: '发现新世界，继续您的阅读之旅，探索我们的广阔藏书。',
      subtitleGuest: '发现数千本图书，管理您的阅读之旅，与读者社区建立联系。',
      searchPlaceholder: '搜索图书、ISBN或作者...',
      searchButton: '搜索'
    },
    quickActions: {
      title: '快捷操作',
      browseBooks: {
        title: '浏览图书',
        description: '探索我们的馆藏'
      },
      myBorrows: {
        title: '我的借阅',
        description: '查看已借图书'
      },
      dashboard: {
        title: '面板',
        description: '您的阅读统计'
      },
      signIn: {
        title: '登录',
        description: '访问您的账户'
      },
      register: {
        title: '注册',
        description: '加入我们的社区'
      }
    },
    categories: {
      title: '按分类浏览'
    },
    recommendedBooks: {
      title: '推荐图书',
      subtitle: '最新添加到我们的馆藏',
      viewAll: '查看全部'
    },
    popularBooks: {
      title: '热门借阅',
      subtitle: '最近最受欢迎的图书'
    },
    libraryFeatures: {
      title: '为什么选择我们的图书馆？',
      subtitle: '发现加入我们数字图书馆社区的好处',
      vastCollection: {
        title: '丰富馆藏',
        description: '访问所有类型和分类的数千本图书。'
      },
      community: {
        title: '社区',
        description: '与其他读者联系，分享您的阅读体验。'
      },
      easyManagement: {
        title: '便捷管理',
        description: '跟踪您的借阅图书并管理您的阅读计划。'
      },
      personalized: {
        title: '个性化',
        description: '根据您的阅读偏好获取推荐。'
      }
    },
    status: {
      available: '可借阅',
      outOfStock: '无库存',
      unknownAuthor: '未知作者',
      available_count: '可借',
      borrows: '次借阅',
    },
    loading: {
      content: '正在加载图书馆内容...',
      error: '加载部分内容失败'
    }
  },
  "routes": {
    "home": "首页",
    "dashboard": "面板",
    "login": "登录",
    "register": "注册",
    "profile": "个人资料",
    "notices": "公告",
    "reservations": "预约",
    "favorites": "收藏",
    "billing": "缴费",
    "api-test": "API测试",
    "not-found": "页面未找到",
    "books": "图书",
    "book-detail": "图书详情",
    "book": {
      "index": "图书",
      "detail": "图书详情",
      "copies": "图书副本",
      "create": "创建图书",
      "edit": "编辑图书"
    },
    "borrows": "我的借阅",
    "admin": {
      "dashboard": "管理员面板",
      "books": "图书管理",
      "book-create": "创建图书",
      "book-edit": "编辑图书",
      "users": "用户管理",
      "borrowing": "借阅管理",
      "borrowing-rules": "借阅规则",
      "fees": "费用管理",
      "reports": "报表"
    }
  },
  books: {
    title: '图书',
    description: '浏览和管理图书馆图书',
    addBook: '添加图书',
    search: {
      label: '搜索',
      placeholder: '搜索图书标题、作者或ISBN...',
      button: '搜索'
    },
    filters: {
      category: {
        label: '分类',
        all: '所有分类'
      },
      language: {
        label: '语言',
        all: '所有语言'
      },
      sort: {
        label: '排序方式',
        titleAsc: '标题 (A-Z)',
        titleDesc: '标题 (Z-A)',
        availableFirst: '优先显示可借',
        totalCopies: '总副本数'
      }
    },
    results: {
      showing: '显示 {total} 本图书中的 {count} 本',
      loading: '正在加载图书...',
      noResults: '未找到符合条件的图书。'
    },
    pagination: {
      previous: '上一页',
      next: '下一页',
      page: '第 {current} 页，共 {total} 页'
    },
    form: {
      title: '添加图书',
      description: '填写新图书的详细信息',
      fields: {
        title: {
          label: '标题',
          placeholder: '请输入图书标题...'
        },
        isbn: {
          label: 'ISBN',
          placeholder: '请输入ISBN...'
        },
        language: {
          label: '语言',
          placeholder: '请输入语言...'
        },
        category: {
          label: '分类',
          placeholder: '请输入分类...'
        },
        authors: {
          label: '作者',
          placeholder: '请输入作者（用逗号分隔）...'
        },
        publisher: {
          label: '出版社',
          placeholder: '请输入出版社...'
        },
        publishedYear: {
          label: '出版年份'
        },
        description: {
          label: '描述',
          placeholder: '请输入图书描述...'
        },
        coverURL: {
          label: '封面图片URL',
          placeholder: 'https://example.com/book-cover.jpg （可选）'
        },
        totalQuantity: {
          label: '总数量'
        }
      },
      buttons: {
        cancel: '取消',
        add: '添加图书',
        adding: '正在添加...'
      },
      validation: {
        requiredFields: '请填写所有必填字段'
      },
      messages: {
        success: '图书添加成功！',
        error: '添加图书失败'
      }
    },
    errors: {
      loadFailed: '加载图书失败'
    }
  }
}
