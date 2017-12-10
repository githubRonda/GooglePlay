# 谷歌电子市场

## 运行项目

- 编码UTF-8

- 导入GooglePlayTeach, android-support-v7-appcompat, XUtilsLibrary, GooglePlayTeach关联appcompat和xutils两个库项目

- 导入WebServer并运行, 这是手机端的服务器项目, 服务器需要资源文件包WebInfos, 需要将WebInfos导入到sdcard根目录,模拟器选中Genymotion, 或者真机. GenyMotion导入文件夹方式: 进入Genymotion桌面, 拖拽文件夹到桌面, 系统会自动拷贝到sdcard/Download文件夹下, 然后移动到sdcard根目录 

## BaseFragment

> 共性

- 加载中
- 加载失败
- 数据为空
- 加载成功

FragmentPagerAdapter是PagerAdapter的子类, 如果viewpager的页面是fragment的话,就继承此类
而且必须要复写的方法只有 getCount() 和 getItem() , 不需要再复写 instantiateItem() 和 destroyItem() 方法了


主界面中每一个Tab都对应一个Fragment, 而每一个Fragment都会在显示的时候创建并使用 LoadingPage 来加载并调整界面中的显示内容


    public static class Person {
        public static Person p1 = new Person();
        public static Person p2 = new Person();
        public static Person p3 = new Person();
    }

	// 枚举就相当于上面这种形式的简化
    public enum  Person {
        p1,
        p2,
        p3;
    }


	// 枚举也是可以添加参数的
    public enum Person {
        p1(10),
        p2(12),
        p3(13);

        private int age;
        private Person(int age){ // 枚举的构造方法必须是私有的, 而且默认也是私有的
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }
	
	// 类似于普通类的如下形式
    public static class Person {
        public static Person p1 = new Person(10);
        public static Person p2 = new Person(12);
        public static Person p3 = new Person(13);

        public Person(int age) {

        }
    }


## LoadingPage的封装

状态:

- 未加载
- 加载中 
- 加载失败 
- 数据为空 
- 加载成功

LoadingPage 的整体逻辑: 一进入界面就请求后台加载数据, 根据请求返回的结果状态加载显示对应的布局.

但是在具体实现上: 先初始化好各个状态的布局, 然后请求服务器加载数据, 根据这个请求返回的结果显示对应的状态布局. (其中 加载成功的布局是由使用者来实现的)

其实这两种逻辑都是可以的, 后者从代码角度来说排版更好看一点!

LoadingPage 请求后台数据成功时, 显示ListView的布局, 但是和ListView的"加载更多"的状态是没有什么关系的


待优化:

 LoadingPage 的加载数据的逻辑的封装

   
	-LoadingPage.java
	/**
     * 异步加载数据
     */
    public void loadData() {
        if (mCurrentState != STATE_LOADING) { // 如果当前没有加载, 就开始加载数据
            mCurrentState = STATE_LOADING;

            new Thread() {
                @Override
                public void run() {

                    final ResultState resultState = onLoad();// onLoad()被Fragment实现,内部调用Fragment#onLoad()方法
                    //主线程中更新UI
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState();// 网络加载结束后,更新网络状态

                                // 根据最新的状态来刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            }.start();
        }
    }

	- HomeFragment
	
	// 运行在子线程,可以直接执行耗时网络操作
    @Override
    public LoadingPage.ResultState onLoad() {
        //请求网络
        mData = new ArrayList<>(); // 每次按返回键退出程序重新进来时, 原有的data数据会保留, Activity虽然会重新创建一个,但是Fragment会恢复, 所以原来的数据会保留
        for (int i = 0; i < 30; i++) {
            mData.add("item " + i);
        }

        return LoadingPage.ResultState.STATE_SUCCESS;
    }

	问题:
	1. 我认为这种仅仅把LoadingPage中在子线程中请求加载数据的逻辑抽象出来由宿主HomeFragment来实现,这种方式不太好. 应该把整个请求服务器的逻辑抽象出来, 而不仅仅是子线程中那部分逻辑. 
		原因就是:1) 职责更加清晰, 不会出现一个请求数据功能分层两部分来完成. 2) 我们封装的网络请求框架也应该类似那种开源框架一样, 子线程中请求数据,然后在主线程中回调方法. 如果使用上面这种封装, 那我们的请求框架仅仅完成的是子线程中请求数据的部分




## ListView Adapter的封装
BaseAdapter中 使用多类型的ItemView时要注意 定义的条目类型(getItemViewType())要从0开始依次递增, 否则AbsListView中就会报ArrayIndexOutOfBoundsException(数组越界异常)

加载更多:
1) 给ListView设置滑动监听, 当滑动到最底部时, 调用加载更多方法.
2) 当Adapter中显示加载更多ItemView时, 调用加载更多方法

这里选择第二种方式, 因为这本身就是Adapter的封装, 而且也可以减少ListView的职责


## 网络封装

- 请求网络获取数据
- 缓存机制(写缓存和读缓存)
- 解析数据

> 请求网络前, 先判断是否有缓存, 有的话就加载缓存


	// android api23之后, 使用HttpClient
	android {
	    ...
	    useLibrary 'org.apache.http.legacy'
	}



BaseAdapter中 使用多类型的ItemView时要注意 定义的条目类型(getItemViewType())要从0开始依次递增, 否则AbsListView中就会报ArrayIndexOutOfBoundsException(数组越界异常)


 SystemClock.sleep(2000); 线程休眠. 内部就是调用 Thread.sleep()方法. 只不过一个是属于android提供,一个是属于java提供