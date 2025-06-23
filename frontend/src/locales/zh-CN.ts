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
  login: {
    title: '登录您的账户',
    subtitle: '欢迎回到图书管理系统',
    form: {
      fields: {
        username: {
          label: '用户名或邮箱',
          placeholder: '请输入用户名或邮箱'
        },
        password: {
          label: '密码',
          placeholder: '请输入密码'
        }
      },
      buttons: {
        signIn: '登录',
        signingIn: '登录中...'
      },
      validation: {
        allFieldsRequired: '请填写所有字段'
      },
      messages: {
        success: '登录成功！',
        successDescription: '欢迎回来，{username}。',
        loginFailed: '登录失败，请重试。',
        unexpectedError: '发生意外错误，请重试。'
      }
    },
    register: {
      text: '还没有账户？',
      link: '在这里注册'
    }
  },
  register: {
    title: '创建您的账户',
    subtitle: '加入图书管理系统',
    form: {
      fields: {
        username: {
          label: '用户名',
          placeholder: '请输入用户名'
        },
        email: {
          label: '邮箱地址',
          placeholder: '请输入邮箱'
        },
        password: {
          label: '密码',
          placeholder: '创建密码'
        },
        confirmPassword: {
          label: '确认密码',
          placeholder: '确认您的密码'
        }
      },
      buttons: {
        createAccount: '创建账户',
        creatingAccount: '正在创建账户...'
      },
      validation: {
        allFieldsRequired: '请填写所有字段',
        validEmail: '请输入有效的邮箱地址',
        passwordLength: '密码长度至少为6个字符',
        passwordMatch: '密码不匹配'
      },
      messages: {
        success: '注册成功！请登录。',
        registrationFailed: '注册失败，请重试。',
        unexpectedError: '发生意外错误，请重试。'
      }
    },
    login: {
      text: '已有账户？',
      link: '在这里登录'
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
  },
  bookCard: {
    loadingImage: '正在加载图片...',
    noImageAvailable: '无图片可用',
    status: {
      available: '可借阅',
      limited: '库存有限',
      outOfStock: '无库存',
      notAvailable: '不可借阅'
    },
    availability: {
      singleCopy: '1本可借',
      multipleCopies: '{count}本可借',
      total: '总计'
    },
    authorPrefix: '作者：',
    isbnPrefix: 'ISBN：'
  },
  notices: {
    title: '图书馆公告',
    description: '了解图书馆公告和最新消息',
    addNotice: '添加公告',
    search: {
      placeholder: '搜索公告...',
      showing: '{total} 条公告中的 {count} 条'
    },
    loading: '正在加载公告...',
    empty: {
      title: '暂无公告',
      description: '目前没有可显示的公告。',
      createFirst: '创建第一条公告'
    },
    noResults: '未找到符合搜索条件的公告。',
    status: {
      new: '新',
      pinned: '置顶',
      show: '显示'
    },
    time: {
      justNow: '刚刚',
      minutesAgo: '{count} 分钟前',
      hoursAgo: '{count} 小时前',
      daysAgo: '{count} 天前',
      updated: '更新于 {time}'
    },
    form: {
      create: {
        title: '创建新公告',
        description: '为图书馆用户创建新的公告。'
      },
      edit: {
        title: '编辑公告',
        description: '更新公告信息。'
      },
      fields: {
        title: {
          label: '标题',
          placeholder: '请输入公告标题...'
        },
        content: {
          label: '内容',
          placeholder: '请输入公告内容...'
        },
        publishTime: {
          label: '发布时间',
          required: '发布时间 *'
        },
        expireTime: {
          label: '过期时间（可选）'
        },
        status: {
          label: '状态',
          required: '状态 *',
          placeholder: '选择状态'
        }
      },
      buttons: {
        cancel: '取消',
        create: '创建公告',
        creating: '正在创建...',
        update: '更新公告',
        updating: '正在更新...'
      },
      validation: {
        requiredFields: '请填写所有必填字段'
      }
    },
    delete: {
      title: '删除公告',
      description: '您确定要删除此公告吗？此操作无法撤销。',
      confirm: '删除',
      cancel: '取消'
    },
    pagination: {
      previous: '上一页',
      next: '下一页',
      page: '第 {current} 页，共 {total} 页'
    },
    messages: {
      createSuccess: '公告创建成功！',
      createError: '创建公告失败',
      updateSuccess: '公告更新成功！',
      updateError: '更新公告失败',
      deleteSuccess: '公告删除成功！',
      deleteError: '删除公告失败',
      loadError: '加载公告失败'
    }
  },
  notFound: {
    title: '404',
    heading: '页面未找到',
    description: '您要访问的页面不存在。可能已被移动、删除，或者您输入了错误的网址。',
    buttons: {
      goBack: '返回',
      goHome: '回到首页'
    }
  },
  favorites: {
    title: '收藏',
    description: '管理您的收藏图书',
    loading: '正在加载收藏...',
    pageTitle: '我的收藏图书',
    count: {
      singular: '您收藏了 {count} 本图书',
      plural: '您收藏了 {count} 本图书'
    },
    empty: {
      title: '暂无收藏',
      description: '开始将图书添加到您的收藏中吧。'
    },
    table: {
      headers: {
        title: '图书标题',
        authors: '作者',
        category: '分类',
        addedDate: '添加日期',
        actions: '操作'
      },
      actions: {
        viewDetails: '查看详情',
        remove: '移除',
        removing: '正在移除...'
      },
      categoryDefault: '未分类',
      isbnPrefix: 'ISBN：'
    },
    dialogs: {
      removeConfirm: '您确定要从收藏中移除这本图书吗？'
    },
    messages: {
      removeSuccess: '已从收藏中移除图书！',
      removeError: '移除收藏失败，请重试。',
      loadError: '加载收藏失败'
    }
  },
  billing: {
    title: '缴费中心',
    description: '管理您的费用和付款',
    loading: '正在加载缴费信息...',
    sections: {
      overdueFees: {
        title: '逾期费用',
        description: '图书逾期归还费用'
      },
      paymentSummary: {
        title: '付款汇总'
      }
    },
    table: {
      headers: {
        borrowRecordId: '借阅记录ID',
        feeAmount: '费用金额',
        status: '状态',
        actions: '操作'
      }
    },
    status: {
      paid: '已付款',
      unpaid: '未付款'
    },
    actions: {
      payFee: '缴费',
      processing: '处理中...'
    },
    summary: {
      unpaidFees: '未付费用',
      totalUnpaidAmount: '总未付金额',
      paidFees: '已付费用'
    },
    empty: {
      noOverdueFees: '未发现逾期费用'
    },
    dialogs: {
      payConfirm: '您确定要支付此费用吗？'
    },
    messages: {
      loadError: '加载缴费信息失败',
      paySuccess: '费用支付成功！',
      payError: '费用支付失败，请重试。'
    }
  },
  borrowing: {
    title: '我的借阅',
    description: '管理您的借阅图书和费用',
    loading: '正在加载您的借阅历史...',
    buttons: {
      browseBooks: '浏览图书',
      refresh: '刷新',
      renew: '续借',
      return: '归还',
      returning: '正在归还...',
      payNow: '立即支付'
    },
    summary: {
      activeBorrows: {
        title: '正在借阅',
        description: '当前借阅中'
      },
      overdueBooks: {
        title: '逾期图书',
        description: '需要立即处理'
      },
      outstandingFees: {
        title: '未付费用',
        description: '未支付的逾期费用'
      }
    },
    filters: {
      search: {
        label: '搜索图书',
        placeholder: '按标题、作者或ISBN搜索...'
      },
      status: {
        label: '状态',
        all: '所有借阅',
        active: '借阅中',
        returned: '已归还',
        overdue: '逾期'
      }
    },
    status: {
      returned: '已归还',
      overdue: '逾期',
      dueSoon: '即将到期',
      active: '借阅中'
    },
    fees: {
      title: '未付费用',
      description: '请支付逾期费用以继续借阅',
      borrowId: '借阅ID：{id}',
      feeDate: '费用日期：{date}'
    },
    history: {
      title: '借阅历史',
      showing: '显示 {total} 条借阅记录中的 {count} 条',
      isbn: 'ISBN：{isbn}',
      borrowed: '借阅时间：{date}',
      due: '到期时间：{date}',
      returned: '归还时间：{date}',
      daysLeft: '还有 {days} 天',
      daysOverdue: '逾期 {days} 天'
    },
    empty: {
      noBorrows: '未找到符合条件的借阅记录',
      noFees: '无未付费用'
    },
    pagination: {
      previous: '上一页',
      next: '下一页',
      page: '第 {current} 页，共 {total} 页'
    },
    messages: {
      loadError: '加载借阅历史失败',
      returnSuccess: '图书归还成功！',
      returnError: '图书归还失败',
      renewSuccess: '图书续借成功！',
      renewError: '图书续借失败',
      paySuccess: '费用支付成功！',
      payError: '费用支付失败'
    }
  },
  bookCopies: {
    title: '图书副本',
    breadcrumb: {
      books: '图书',
      copies: '副本'
    },
    header: {
      title: '图书副本',
      addCopy: '添加副本'
    },
    loading: {
      bookDetails: '正在加载图书详情...',
      error: '加载图书错误',
      tryAgain: '重试'
    },
    dialogs: {
      create: {
        title: '添加新副本',
        description: '创建《{title}》的新副本',
        fields: {
          barcode: {
            label: '条形码',
            placeholder: '请输入条形码...'
          },
          status: {
            label: '状态',
            placeholder: '选择状态'
          },
          purchasePrice: {
            label: '购买价格（可选）',
            placeholder: '0.00'
          }
        },
        buttons: {
          cancel: '取消',
          create: '创建副本'
        }
      },
      edit: {
        title: '编辑副本',
        description: '更新副本详情',
        fields: {
          barcode: {
            label: '条形码',
            placeholder: '请输入条形码...'
          },
          status: {
            label: '状态',
            placeholder: '选择状态'
          },
          purchasePrice: {
            label: '购买价格',
            placeholder: '0.00'
          }
        },
        buttons: {
          cancel: '取消',
          update: '更新副本'
        }
      }
    },
    status: {
      available: '可借阅',
      borrowed: '已借出',
      maintenance: '维护中',
      scrapped: '已报废',
      discarded: '已丢弃'
    },
    messages: {
      createSuccess: '副本创建成功',
      createError: '创建副本失败',
      updateSuccess: '副本更新成功',
      updateError: '更新副本失败',
      borrowSuccess: '图书借阅成功',
      borrowError: '图书借阅失败',
      returnSuccess: '图书归还成功',
      returnError: '图书归还失败',
      maintenanceSuccess: '副本状态已更新为维护中',
      maintenanceError: '更新副本状态失败',
      loginRequired: '请登录后借阅图书',
      loginRequiredReturn: '请登录后归还图书',
      noBorrowRecord: '未找到此图书副本的有效借阅记录'
    }
  },
  adminBorrowing: {
    title: '管理员借阅管理',
    description: '为用户注册和管理图书借阅',
    buttons: {
      registerBorrow: '注册借阅',
      cancel: '取消',
      creating: '正在创建...',
      refresh: '刷新'
    },
    summary: {
      totalBorrows: {
        title: '总借阅数',
        description: '所有借阅记录'
      },
      activeBorrows: {
        title: '活跃借阅',
        description: '当前借阅中'
      },
      overdueBooks: {
        title: '逾期图书',
        description: '需要注意'
      }
    },
    dialog: {
      title: '注册图书借阅',
      description: '选择用户和图书副本以注册新的借阅记录',
      userSelection: {
        label: '选择用户',
        placeholder: '按用户名搜索用户...',
        idPrefix: 'ID：'
      },
      bookSelection: {
        label: '选择图书',
        placeholder: '按标题、作者或ISBN搜索图书...',
        available: '可借：{available} / {total}',
        isbnPrefix: 'ISBN：'
      },
      copySelection: {
        label: '选择可借副本',
        copyNumber: '副本 #{id}',
        availableStatus: '可借阅',
        noAvailableCopies: '此图书无可借副本'
      }
    },
    filters: {
      search: {
        label: '搜索记录',
        placeholder: '按用户、图书标题或ISBN搜索...'
      },
      status: {
        label: '状态',
        allBorrows: '所有借阅',
        active: '借阅中',
        returned: '已归还',
        overdue: '逾期'
      }
    },
    list: {
      title: '所有借阅记录',
      description: '显示 {total} 条记录中的 {count} 条',
      loading: '正在加载借阅记录...',
      empty: '未找到借阅记录',
      copyPrefix: '副本 #',
      isbnPrefix: 'ISBN：',
      borrowed: '借阅时间：{date}',
      due: '到期时间：{date}',
      returned: '归还时间：{date}',
      daysLeft: '还有 {days} 天',
      daysOverdue: '逾期 {days} 天'
    },
    status: {
      returned: '已归还',
      overdue: '逾期',
      dueSoon: '即将到期',
      active: '借阅中'
    },
    messages: {
      success: '图书借阅成功！',
      error: '创建借阅记录失败',
      loadBorrowsError: '加载借阅记录失败',
      loadUsersError: '加载用户失败',
      loadBooksError: '加载图书失败',
      loadCopiesError: '加载图书副本失败',
      validationError: '请同时选择用户和图书副本'
    }
  },
  bookForm: {
    title: {
      create: '添加新图书',
      edit: '编辑图书'
    },
    description: {
      create: '向图书馆添加新图书',
      edit: '更新图书信息'
    },
    loading: {
      book: '正在加载图书数据...',
      saving: '正在保存...'
    },
    sections: {
      basicInfo: {
        title: '基本信息',
        description: '输入图书的基本详情'
      },
      authors: {
        title: '作者',
        description: '添加图书的作者'
      },
      publishers: {
        title: '出版社',
        description: '添加图书的出版社'
      },
      category: {
        title: '分类',
        description: '指定图书的分类'
      }
    },
    fields: {
      title: {
        label: '标题',
        placeholder: '请输入图书标题',
        required: '标题 *'
      },
      isbn: {
        label: 'ISBN',
        placeholder: '请输入ISBN',
        required: 'ISBN *'
      },
      language: {
        label: '语言',
        placeholder: '例如：中文、英文等',
        required: '语言 *'
      },
      location: {
        label: '位置',
        placeholder: '例如：LIBRARY、STORAGE等'
      },
      totalQuantity: {
        label: '总数量',
        required: '总数量 *'
      },
      description: {
        label: '描述',
        placeholder: '请输入图书描述（可选）'
      },
      coverURL: {
        label: '封面图片URL',
        placeholder: 'https://example.com/book-cover.jpg （可选）'
      },
      category: {
        label: '分类',
        placeholder: '请输入分类名称',
        required: '分类 *'
      },
      newAuthor: {
        placeholder: '请输入作者姓名'
      },
      newPublisher: {
        placeholder: '请输入出版社名称'
      }
    },
    buttons: {
      back: '返回',
      cancel: '取消',
      save: '保存',
      create: '创建图书',
      update: '更新图书',
      creating: '正在创建...',
      updating: '正在更新...',
      add: '添加',
      remove: '移除'
    },
    validation: {
      titleRequired: '标题为必填项',
      isbnRequired: 'ISBN为必填项',
      languageRequired: '语言为必填项',
      authorsRequired: '至少需要一个作者',
      publishersRequired: '至少需要一个出版社',
      categoryRequired: '分类为必填项',
      quantityMinimum: '总数量至少为1'
    },
    messages: {
      loadError: '加载图书数据失败',
      saveError: '保存图书失败',
      createSuccess: '图书创建成功！',
      updateSuccess: '图书更新成功！'
    }
  },
  dashboard: {
    greeting: {
      morning: '上午好',
      afternoon: '下午好',
      evening: '晚上好',
      subtitle: '准备好探索您的图书馆了吗？'
    },
    loading: '正在加载您的面板...',
    user: {
      roles: {
        admin: '管理员',
        user: '读者'
      },
      memberSince: '注册时间',
      recently: '最近'
    },
    stats: {
      availableBooks: {
        title: '可借图书',
        description: '可以借阅'
      },
      activeLoans: {
        title: '您的借阅中',
        description: '当前借阅中'
      },
      booksRead: {
        title: '已读图书',
        description: '成功归还'
      },
      overdueBooks: {
        title: '逾期图书',
        description: '需要归还'
      }
    },
    quickActions: {
      title: '快捷操作',
      subtitle: '您今天想做什么？',
      browseBooks: {
        title: '浏览图书',
        description: '探索可借图书'
      },
      myBorrowings: {
        title: '我的借阅',
        description: '查看借阅历史'
      },
      myProfile: {
        title: '我的资料',
        description: '管理您的账户'
      },
      readingList: {
        title: '阅读清单',
        description: '管理您的愿望清单'
      }
    },
    newArrivals: {
      title: '新书推荐',
      description: '最新添加到图书馆',
      browseAll: '浏览全部',
      noBooks: '暂无最新图书'
    },
    recentActivity: {
      title: '您的最近活动',
      description: '您的借阅历史',
      viewAll: '查看全部',
      notAuthenticated: '请登录查看您的活动',
      noActivity: '暂无借阅活动',
      startBrowsing: '开始浏览图书',
      returned: '已归还',
      due: '到期'
    },
    notices: {
      title: '图书馆公告',
      description: '重要更新和公告',
      viewAll: '查看全部',
      noNotices: '暂无最新公告'
    },
    recommendations: {
      title: '为您推荐',
      description: '基于您的阅读历史和偏好',
      comingSoon: '个性化推荐即将推出！',
      keepReading: '继续借阅图书，帮助我们了解您的偏好。'
    },
    messages: {
      loadError: '加载统计数据失败'
    }
  },
  bookDetail: {
    title: '图书详情',
    backToBooks: '返回图书列表',
    loading: '正在加载图书详情...',
    cover: {
      noImageAvailable: '无封面图片',
      altText: '《{title}》封面'
    },
    availability: {
      available: '可借阅',
      limited: '库存有限',
      outOfStock: '无库存',
      notAvailable: '不可借阅',
      singleCopy: '1本可借',
      multipleCopies: '{count}本可借',
      totalCopies: '总副本数：{count}'
    },
    rating: {
      singleReview: '条评价',
      multipleReviews: '{count}条评价'
    },
    buttons: {
      borrow: '借阅图书',
      borrowing: '正在借阅...',
      viewCopies: '查看副本',
      share: '分享',
      edit: '编辑图书'
    },
    details: {
      title: '图书详情',
      isbn: 'ISBN：',
      category: '分类：',
      publishers: '出版社：',
      language: '语言：',
      available: '可借：',
      uncategorized: '未分类'
    },
    description: {
      title: '简介'
    },
    comments: {
      title: '评价与评论',
      description: '看看其他人对这本书的看法'
    },
    notFound: {
      title: '图书未找到',
      description: '您查找的图书不存在。',
      backButton: '返回图书列表'
    },
    messages: {
      loadError: '加载图书详情失败',
      borrowSuccess: '图书借阅成功！',
      borrowError: '图书借阅失败',
      borrowLoginRequired: '请登录后借阅图书',
      borrowNotAvailable: '此图书暂不可借阅',
      shareSuccess: '链接已复制到剪贴板！'
    }
  },
  adminDashboard: {
    title: '管理员面板',
    subtitle: '系统概览和管理控制',
    administrator: '管理员',
    loading: '正在加载管理员面板...',
    stats: {
      totalUsers: {
        title: '总用户数',
        description: '已注册用户'
      },
      activeLoans: {
        title: '活跃借阅',
        description: '当前借阅中'
      },
      overdueBooks: {
        title: '逾期图书',
        description: '需要注意'
      },
      totalBooks: {
        title: '图书总数',
        description: '图书馆中'
      }
    },
    actions: {
      title: '管理员操作',
      description: '常用管理任务和系统管理',
      addNewBook: {
        title: '添加新图书',
        description: '向图书馆添加图书'
      },
      manageUsers: {
        title: '用户管理',
        description: '用户管理'
      },
      borrowingRules: {
        title: '借阅规则',
        description: '配置借阅规则'
      },
      feeManagement: {
        title: '费用管理',
        description: '管理费用和罚款'
      },
      systemReports: {
        title: '系统报表',
        description: '生成报表'
      },
      manageNotices: {
        title: '公告管理',
        description: '系统公告'
      }
    },
    sections: {
      recentBooks: {
        title: '最近添加的图书',
        description: '图书馆的最新添加',
        manageAll: '管理全部',
        noBooks: '未找到最近的图书',
        unknownAuthor: '未知作者'
      },
      overdueBorrows: {
        title: '逾期借阅',
        description: '需要管理员注意的图书',
        manageAll: '管理全部',
        noOverdue: '无逾期借阅',
        userPrefix: '用户：',
        duePrefix: '到期：',
        unknownUser: '未知用户',
        unknownBook: '未知图书'
      },
      systemNotices: {
        title: '系统公告',
        description: '管理公告和系统消息',
        createNotice: '创建公告',
        manageAll: '管理全部',
        noNotices: '无最近公告',
        untitled: '无标题',
        noContent: '无内容',
        publishedPrefix: '发布时间：',
        edit: '编辑'
      },
      bookInventory: {
        title: '图书库存分类统计',
        description: '各分类图书可用性统计',
        manageInventory: '管理库存',
        noData: '无库存数据可用',
        totalPrefix: '总计：',
        availablePrefix: '可借：',
        borrowedPrefix: '已借：',
        availableSuffix: '% 可借'
      }
    },
    status: {
      returned: '已归还',
      overdue: '逾期',
      dueSoon: '即将到期',
      active: '借阅中'
    },
    messages: {
      loadStatisticsError: '加载统计数据失败',
      loadSystemStatsError: '加载部分系统统计数据失败',
      loadRecentBooksError: '加载最近图书失败',
      loadOverdueBorrowsError: '加载逾期借阅失败',
      loadRecentNoticesError: '加载最近公告失败',
      loadInventoryStatsError: '加载库存统计数据失败'
    }
  },
  userManagement: {
    title: '用户管理',
    description: '管理图书馆用户及其权限',
    addUser: '添加用户',
    totalUsers: '位用户',
    noUsersFound: '未找到用户',
    createNewUser: '创建新用户',
    addNewUser: '向图书管理系统添加新用户。',
    username: '用户名',
    email: '邮箱',
    password: '密码',
    role: '角色',
    user: '用户',
    created: '创建时间',
    lastUpdated: '最后更新',
    actions: '操作',
    createUser: '创建用户',
    editUser: '编辑用户',
    updateUser: '更新用户',
    updateUserInfo: '更新用户信息',
    updateUserRole: '更新用户角色',
    changeRole: '更改角色',
    newRole: '新角色',
    updateRole: '更新角色',
    deleteUser: '删除用户',
    sureDelete: '您确定要删除',
    actionCannotBeUndone: '此操作无法撤销。',
    willRemoveAllUserData: '这将删除所有用户数据。',
    cancel: '取消',
    showing: '显示',
    to: '到',
    of: '共',
    users: '位用户',
    page: '第',
    next: '下一页',
    previous: '上一页',
    search: {
      placeholder: '按用户名或邮箱搜索用户...',
      showing: '共 {count} 位用户'
    },
    table: {
      headers: {
        user: '用户',
        email: '邮箱',
        role: '角色',
        created: '创建时间',
        lastUpdated: '最后更新',
        actions: '操作'
      },
      noEmail: '未提供'
    },
    roles: {
      admin: '管理员',
      user: '用户'
    },
    pagination: {
      showing: '显示第 {start} 到 {end} 项，共 {total} 位用户',
      page: '第 {current} 页，共 {total} 页',
      previous: '上一页',
      next: '下一页'
    },
    empty: {
      title: '未找到用户',
      searchDescription: '请尝试调整搜索条件',
      defaultDescription: '开始创建您的第一个用户'
    },
    dialogs: {
      create: {
        title: '创建新用户',
        description: '向图书管理系统添加新用户。',
        fields: {
          username: {
            label: '用户名',
            placeholder: '请输入用户名'
          },
          email: {
            label: '邮箱',
            placeholder: '请输入邮箱地址'
          },
          password: {
            label: '密码',
            placeholder: '请输入密码'
          },
          role: {
            label: '角色',
            placeholder: '选择角色'
          }
        },
        buttons: {
          cancel: '取消',
          create: '创建用户'
        }
      },
      edit: {
        title: '编辑用户',
        description: '更新 {username} 的用户信息。',
        fields: {
          username: {
            label: '用户名',
            placeholder: '请输入用户名'
          },
          email: {
            label: '邮箱',
            placeholder: '请输入邮箱地址'
          }
        },
        buttons: {
          cancel: '取消',
          update: '更新用户'
        }
      },
      role: {
        title: '更新用户角色',
        description: '更改 {username} 的角色。',
        fields: {
          newRole: {
            label: '新角色',
            placeholder: '选择新角色'
          }
        },
        buttons: {
          cancel: '取消',
          update: '更新角色'
        }
      },
      delete: {
        title: '删除用户',
        description: '您确定要删除 {username} 吗？此操作无法撤销，将删除所有用户数据。',
        buttons: {
          cancel: '取消',
          delete: '删除用户'
        }
      }
    },
    messages: {
      validation: {
        requiredFields: '请填写所有必填字段'
      },
      success: {
        userCreated: '用户创建成功',
        userUpdated: '用户更新成功',
        roleUpdated: '用户角色更新成功',
        userDeleted: '用户删除成功'
      },
      error: {
        createFailed: '创建用户失败',
        updateFailed: '更新用户失败',
        roleUpdateFailed: '更新用户角色失败',
        deleteFailed: '删除用户失败',
        loadFailed: '加载用户失败'
      }
    }
  },
  profile: {
    title: '用户资料',
    description: '管理您的个人信息和设置',
    loading: {
      profile: '正在加载个人资料...',
      data: '正在加载用户数据...',
      borrowing: '正在加载借阅数据...'
    },
    header: {
      memberSince: '注册时间：{date}',
      memberSinceLoading: '注册时间：加载中...'
    },
    sections: {
      profileInfo: {
        title: '个人信息',
        description: '管理您的个人信息'
      },
      security: {
        title: '安全设置',
        description: '管理您的账户安全'
      },
      borrowingStats: {
        title: '借阅统计',
        description: '您的图书馆活动概览'
      },
      recentActivity: {
        title: '最近借阅活动',
        description: '您最近的图书借阅活动'
      }
    },
    fields: {
      username: {
        label: '用户名',
        placeholder: '请输入您的用户名'
      },
      email: {
        label: '邮箱',
        placeholder: '请输入您的邮箱地址'
      },
      registrationDate: {
        label: '注册日期'
      },
      lastUpdated: {
        label: '最后更新'
      },
      currentPassword: {
        label: '当前密码',
        placeholder: '请输入当前密码'
      },
      newPassword: {
        label: '新密码',
        placeholder: '请输入新密码'
      },
      confirmPassword: {
        label: '确认新密码',
        placeholder: '请确认新密码'
      }
    },
    buttons: {
      editProfile: '编辑资料',
      saveChanges: '保存更改',
      saving: '保存中...',
      cancel: '取消',
      changePassword: '修改密码',
      changingPassword: '修改中...',
      viewAllBorrows: '查看全部'
    },
    stats: {
      totalBorrows: '总借阅',
      returned: '已归还',
      active: '借阅中',
      overdue: '逾期'
    },
    activity: {
      noActivity: '暂无借阅活动',
      due: '到期：{date}',
      dueDate: '到期日期 {date}'
    },
    status: {
      returned: '已归还',
      overdue: '逾期',
      dueSoon: '即将到期',
      active: '借阅中'
    },
    security: {
      lastPasswordUpdate: '您的密码最后更新时间：{date}',
      lastPasswordUpdateDefault: '您的密码最后更新时间：暂无'
    },
    messages: {
      profileUpdateSuccess: '个人资料更新成功！',
      profileUpdateError: '更新个人资料失败',
      passwordChangeSuccess: '密码修改成功！请使用新密码登录。',
      passwordChangeError: '修改密码失败',
      passwordMismatch: '新密码不匹配',
      passwordTooShort: '新密码至少需要6个字符',
      loadUserDataError: '加载用户资料失败',
      loadBorrowingDataError: '加载借阅数据失败'
    },
    placeholders: {
      notAvailable: '暂无'
    }
  }
}
