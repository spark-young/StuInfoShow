# 项目功能简析：

后端开放API，浏览器和App请求数据，后端查找数据库并返回相应数据；前端接收到数据后组合成列表并分页显示

# 第一节——建立数据库表：

在阿里云服务器建立student表

表的结构如下：

| Field     | Type        | Null | Key  | Default | Extra |
| --------- | ----------- | ---- | ---- | ------- | ----- |
| xh        | varchar(10) | NO   | PRI  | NULL    |       |
| name      | varchar(10) | YES  |      | NULL    |       |
| sex       | varchar(2)  | YES  |      | NULL    |       |
| className | varchar(16) | YES  |      | NULL    |       |
# 弟二节——创建后端项目：

创建SpringBoot项目，依赖如下：

```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.1.2</version>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<!--mybatis依赖 -->
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.3.2</version>
		</dependency>
		<!-- 连接池 -->
    <!-- Druid 数据连接池依赖 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
    <!-- mybatis的分页插件 -->
	<dependency>
		<groupId>com.github.pagehelper</groupId>
		<artifactId>pagehelper-spring-boot-starter</artifactId>
		<version>1.2.3</version>
	</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<!-- <dependency>  
			<groupId>org.springframework.boot</groupId>  
			<artifactId>spring-boot-starter-log4j</artifactId>
			<version>1.3.8.RELEASE</version>
		</dependency>  -->
	</dependencies>
```

配置application.yml：

```yml
spring:
  mvc:
    view:
      prefix: /
      suffix: .html
  freemarker:
    suffix: .html
    template-loader-path:
    - classpath:/static/
  datasource:
#    password: 123456789
#    username: Spark
#    url: jdbc:mysql://47.115.112.103:3306/test
#上面是直接连接数据库，下面是druid连接池连接数据库
    druid:
      url: jdbc:mysql://47.115.112.103:3306/test?useUnicode=true&characterEncoding=utf-8
      username: Spark
      password: 123456789
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 6000
  http:
    encoding:
      charset: UTF-8
      force: true
      enabled: true
server:
  tomcat:
    uri-encoding: UTF-8
mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  check-config-location: true
pageSize: 2 #单页大小
```

配置PageHelper分页插件：

```java
@Configuration
public class PageHelperCfg {
    //配置mybatis的分页插件pageHelper
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        properties.setProperty("dialect","mysql");    //配置mysql数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
```



# 第三节——后台API接口程序编写：

编写Mysql API的Mapper，并对相应的方法注解其Sql执行语句

Mybatis可以在Mapper类方法上直接注解，以替代Mapper.xml

@Select、@Insert、@Update、@Delete

```java
public interface StudentMapper {
	//所有学生
	@Select("select * from test.student")
	public List<Student> selectAll();
	//根据xh查询
	@Select("select * from test.student where xh=#{xh}")
	public List<Student> getByXh(String id);
	//根据姓名查询
	@Select("select * from test.student where name=#{name}")
	public List<Student> getByName(String name);
	//根据性别查询
	@Select("select * from test.student where sex=#{sex}")
	public List<Student> getBySex(String sex);
	//根据班级查询
	@Select("select * from test.student where className=#{className}")
	public List<Student> getByClassName(String className);
	//添加学生
	@Insert("insert into test.student values(xh,name,sex,className)")
	public boolean addStu();
	//修改学生信息
	@Update("update test.student set name=#{name},sex=#{sex},className=#{className} where xh=#{xh}")
	public boolean updateStu(Student student);
	//删除学生信息
	@Delete("delete from test.student where xh=#{xh}")
	public boolean deleteStu(String xh);
}
```

Mapper编写完成后，再定义service.StuService接口，接口内容与Mapper的方法一致，用于Controller，并实现为service.Impl.StuServiceImpl类

```java
public interface StuService {
	public List<Student> selectAll();
	public List<Student> selectPage(String page,int pageSize);
	public List<Student> getByXh(String id);
	public List<Student> getByName(String name);
	public List<Student> getBySex(String sex);
	public List<Student> getByClassName(String className);
	public boolean addStu(Student student);
	public boolean updateStu(Student student);
	public boolean deleteStu(String xh);
}
```

StuServiceImpl的实现类，@Service注解表示这是一个服务，@Transactional注解表明这里需要事务处理，因为装配的是持久化类的实例（不注解@Transactional会出现JDBC connection [com. alibaba. druid. proxy. jdbc. ConnectionProxyImpl @XXX] will not be managed by Spring）

```java
@Service
@Transactional
public class StuServiceImpl implements StuService {
	@Autowired
	private StudentMapper studentDao;
    @Autowired//pageHelper
	private PageHelper pageHelper;
	@Override
	public List<Student> selectPage(String page,int pageSize){
		pageHelper.startPage(Integer.valueOf(page), pageSize);
		return studentDao.selectAll();
	}
	@Override
	public List<Student> selectAll() {
		return studentDao.selectAll();
	}
	@Override
	public List<Student> getByXh(String id) {
		return studentDao.getByXh(id);
	}
	@Override
	public List<Student> getByName(String name) {
		return studentDao.getByName(name);
	}
	@Override
	public List<Student> getBySex(String sex) {
		return studentDao.getBySex(sex);
	}
	@Override
	public List<Student> getByClassName(String className) {
		return studentDao.getByClassName(className);
	}
	@Override
	public boolean addStu(Student student) {
		return studentDao.addStu(student);
	}
	@Override
	public boolean updateStu(Student student) {
		return studentDao.updateStu(student);
	}
	@Override
	public boolean deleteStu(String xh) {
		return studentDao.deleteStu(xh);
	}	
}
```

之后实现对前端的API，这里使用Restful API：

```java
@RestController
@RequestMapping("/StuInfo")
public class MainController {
	private static final String list="List";
	@Autowired	
	private StuService stuService;//装配Student的服务类
	
	@GetMapping//uri:http://localhost:8080/StuInfo
	public List<Student> selectAll() {
		return stuService.selectAll();
	}
	@GetMapping("/{type}")
    //uri:http://localhost:8080/StuInfo/xh?param=XXX
	//uri:http://localhost:8080/StuInfo/name?param=XXX
    //uri:http://localhost:8080/StuInfo/sex?param=XXX
    //uri:http://localhost:8080/StuInfo/className?param=XXX
    //uri:http://localhost:8080/StuInfo/page?param=XXX
    public List<Student> getByParam(@PathVariable("type")String type,@RequestParam("param")String param) {
		switch(type){
			case "xh":return stuService.getByXh(param);
			case "name":return stuService.getByName(param);
			case "sex":return stuService.getBySex(param);
			case "className":return stuService.getByClassName(param);
			case "page":return stuService.selectPage(param,pageSize);
		}
		return null;
	}
	@PostMapping
	public boolean addStu(@RequestBody Student student) {
		return stuService.addStu(student);
	}
	@PutMapping
	public boolean updateStu(@RequestBody Student student) {
		return stuService.updateStu(student);
	}
	@DeleteMapping
	public boolean deleteStu(@RequestBody Student student) {
		return stuService.deleteStu(student.getXh());
	}
}
```

# 第四节——API测试：

这里使用FireFox的RestClient插件进行测试：

Get——selectAll，uri:http://localhost:8080/StuInfo：

![image-20200508133357319](/md-img/image-20200508133357319.png)

![image-20200508133453010](/md-img/image-20200508133453010.png)

Get——getByParam，uri:http://localhost:8080/StuInfo/xh?param=XXX：

![image-20200508133729750](/md-img/image-20200508133729750.png)

![image-20200508133747553](/md-img/image-20200508133747553.png)

Get——getByParam，uri:http://localhost:8080/StuInfo/name?param=XXX：

![image-20200508145642564](/md-img/image-20200508145642564.png)

![image-20200508145712527](/md-img/image-20200508145712527.png)

Get——getByParam，uri:http://localhost:8080/StuInfo/sex?param=XXX：

![image-20200508150107034](/md-img/image-20200508150107034.png)

![image-20200508150118450](/md-img/image-20200508150118450.png)

Get——getByParam，uri:http://localhost:8080/StuInfo/className?param=XXX：

![image-20200508150943024](/md-img/image-20200508150943024.png)

![image-20200508150952300](/md-img/image-20200508150952300.png)

Get——getByParam，uri:http://localhost:8080/StuInfo/page?param=XXX：

![image-20200512161553242](/md-img/image-20200512161553242.png)

![image-20200512161728810](/md-img/image-20200512161728810.png)

![image-20200512161757996](/md-img/image-20200512161757996.png)

![image-20200512161808534](/md-img/image-20200512161808534.png)

Post——addStu，uri:http://localhost:8080/StuInfo：

![image-20200508152611351](/md-img/image-20200508152611351.png)

![image-20200508152628959](/md-img/image-20200508152628959.png)

![image-20200508153958192](/md-img/image-20200508153958192.png)

Put——updateStu，uri:http://localhost:8080/StuInfo：

![image-20200508154153896](/md-img/image-20200508154153896.png)

![image-20200508154203603](/md-img/image-20200508154203603.png)

![image-20200508154221598](/md-img/image-20200508154221598.png)

Delete——deleteStu，uri:http://localhost:8080/StuInfo：

![image-20200508154725584](/md-img/image-20200508154725584.png)

![image-20200508154733666](/md-img/image-20200508154733666.png)

![image-20200508155030398](/md-img/image-20200508155030398.png)

经测试，所有Restful API功能正常

# 第五节——Vue+ElementUI实现前端：

使用Vue+ElementUI实现，开发工具为HbuilderX

通过HbuilderX新建工程，选择ElementUI模板。引入vue.min.js，用于Vue前端项目开发；引入axios.min.js，用于在Vue项目中发送Ajax请求：

![image-20200515001955662](/md-img/image-20200515001955662.png)

在线引用element-ui的样式：

![image-20200515002122660](/md-img/image-20200515002122660.png)

在<a href="/element.eleme.cn">element官网</a>上找一个喜欢的表格样式:

![image-20200515003813929](/md-img/image-20200515003813929.png)

复制代码，进行一定的修改：

```html
<el-table v-loading="loading" element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)" :data="tableData" style="width: 100%">>
                <el-table-column prop="xh" label="学号" width="180">
                </el-table-column>
                <el-table-column prop="name" label="姓名" width="180">
                </el-table-column>
                <el-table-column prop="sex" label="性别" width="180">
                </el-table-column>
                <el-table-column prop="className" label="班级">
                </el-table-column>
            </el-table>
```

选择一个喜欢的分页控件：

![image-20200515003939958](/md-img/image-20200515003939958.png)

复制代码，并做一定的修改：

```html
<el-pagination page-size="5" background layout="prev, pager, next" :total="ttl"
                @current-change="handleCurrentChange">
            </el-pagination>
```

对于Vue项目，必然会有以下的script：

```javascript
<script>
    new Vue({
        el: '#app',
        data: {}
    });
</script>
```

我们要给data绑定数据，以便在表格中显示，这里需要我们向服务器请求数据，然而Vue本身是不支持的，所以采用了axios插件。axios是对ajax的封装，使用起来更加便捷。

由于我们需要在进入页面时就获取当前数据库总行数信息，我们需要先向后台请求一次selectAll：

```javascript
axios({
                methods: 'get',
                url: 'StuInfo'
}).then(function (res) {
                //   //把从json获取的数据赋值给数组
                tData = res.data;
                //从结果中获取数据条数信息并设置分页器
                self.ttl = tData.length;
			}
```

这个需要在一开始进入页面，即Vue对象创建时执行一次，因此将此请求放在created函数中，使用页面加载时进行请求且执行一次

对于分页器，我们期望在对分页器进行操作后，可以改变表格的显示，这里需要我们对分页器的Current-Change事件进行监听，一旦发生变化，就按照当前页码使用axios向后端请求：

```javascript
handleCurrentChange(val) {
                var self = this;
                self.loading = true;
                axios({
                    methods: 'get',
                    url: 'StuInfo/page?param=' + val
                    //url: 'StuInfo/page?param=1'
                }).then(function (res) {
                    //把从json获取的数据赋值给数组
                    self.tableData = res.data;
                    //获取到数据之后就关闭loading遮罩
                    self.loading = false;
                });
            }
```

由于请求数据库时时间较长，因此我们对使用的el-table设置loading，这是一个表示等待的遮罩，为true则等待，为false则此遮罩关闭，那么我们等待主要是在axios请求数据时，所以我们每次在axios请求之前开启loading遮罩，在请求完毕之后关闭遮罩

以下是页面完整代码:

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>element-starter</title>
    <!-- 引入样式 -->
    <!-- <link rel="stylesheet" href="/css/eui.index.css"> -->
    <link rel="stylesheet" href="https://unpkg.com/element-ui@2.13.1/lib/theme-chalk/index.css">
    ]<script src="/js/vue.min.js" type="text/javascript" charset="UTF-8"></script>
    <script src="/js/axios.min.js" type="text/javascript" charset="UTF-8"></script>
    <!-- 引入组件库 -->
    <script src="js/element-ui.index.js" type="text/javascript" charset="UTF-8"></script>
</head>

<body>
    <div id="app">
        <template>
            <el-table v-loading="loading" element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)" :data="tableData" style="width: 100%">>
                <el-table-column prop="xh" label="学号" width="180">
                </el-table-column>
                <el-table-column prop="name" label="姓名" width="180">
                </el-table-column>
                <el-table-column prop="sex" label="性别" width="180">
                </el-table-column>
                <el-table-column prop="className" label="班级">
                </el-table-column>
            </el-table>
            <el-pagination page-size="5" background layout="prev, pager, next" :total="ttl"
                @current-change="handleCurrentChange">
            </el-pagination>
        </template>
        <style>
            body {
                margin: 0;
            }
        </style>
</body>

<script>
    new Vue({
        el: '#app',
        data: function () {
            return {
                tableData: [],
                loading: true
            }
        },
        created: function () {
            //为了在内部函数能使用外部函数的this对象，要给它赋值了一个名叫self的变量。
            var self = this;
            var tData = [];//作为缓冲变量，承接axios请求‘StuInfo’的结果，用于获取数据条数
            axios({
                methods: 'get',
                url: 'StuInfo'
            }).then(function (res) {
                //   //把从json获取的数据赋值给数组
                tData = res.data;
                //从结果中获取数据条数信息并设置分页器
                self.ttl = tData.length;
                //初始加载第一页
                axios({
                    methods: 'get',
                    url: 'StuInfo/page?param=1'
                    //url: 'StuInfo/page?param=1'
                }).then(function (res) {
                    //把从json获取的数据赋值给数组
                    self.tableData = res.data;
                    //获取到数据之后就关闭loading遮罩
                    self.loading = false;
                });
            });
        },
        methods: {
            //用于处理Pagination当前页更改事件
            handleCurrentChange(val) {
                var self = this;
                self.loading = true;
                axios({
                    methods: 'get',
                    url: 'StuInfo/page?param=' + val
                    //url: 'StuInfo/page?param=1'
                }).then(function (res) {
                    //把从json获取的数据赋值给数组
                    self.tableData = res.data;
                    //获取到数据之后就关闭loading遮罩
                    self.loading = false;
                });
            }
        }
    });
</script>

</html>
```

### 效果：

1（进入）：

![image-20200515005411720](/md-img/image-20200515005411720.png)

2（加载）：

![image-20200515005453095](/md-img/image-20200515005453095.png)

3（加载完毕）：

![image-20200515005511782](/md-img/image-20200515005511782.png)

# 第六节——git发布到Github并在linux上部署：

到第五节为止，我们的项目已经是一个完整的web项目，可以直接运行并用浏览器访问。

但是看到的是，目前在前端实现的功能仅仅为分页查看学生信息，要实现CRUD的页面操作，即在前端不断加上新的功能，我们可以先将目前的项目发布到Github，并在云服务器上部署，之后新功能失陷后再发布到GitHub，在linux上重新部署即可。

首先我们将此项目发布到GitHub中，发布细节见<a href="http://spark-young.space/wordpress/index.php/2020/05/15/spark/gitshangchuanxiangmudaogithub/">此链接</a>。

将此项目发布到GitHub之后，准备将其部署在云服务器上，此时直接想到的是将springboot项目打成jar包，然后运行jar包：

首先从GitHub上下载项目，点击 Clone or Download --> Clone with HTTPS，右键单击Download ZIP复制链接地址，然后在linux终端上执行：wget https://github.com/spark-young/StuInfoShow/archive/master.zip，即将GitHzaiub下载到云服务器上

### 注意：下载的repository必须是public，否则下载失败提示404

下载完成后在服务器端打包，进入项目根目录，执行：**mvn -clean**清除一次，然后执行：**mvn package**（过程略长），然后在target目录中可以看到已经生成的jar包，jar包名为pom.xml中如下代码段中定义：

```xml
<build>
		<finalName>StuInfoShow</finalName>//<!-- jar包名 -->
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

使用**java -jar StuInfoShow.jar**执行即可运行，访问：http://spark-young.space:8181/listUI 即可

![image-20200515223917198](/md-img/image-20200515223917198.png)

此时有个问题，我们希望部署之后是在Linux后端运行，那么我们运行jar时需要在后面加上“ & ”，即：**java -jar StuInfoShow.jar &**，此时既是在后端运行（其中3006为进程ID）

![image-20200515224044223](/md-img/image-20200515224044223.png)

但还是存在问题，springboot项目运行时会产生一些控制台输出，即使现在是后端运行，在前端发送请求后后端处理时，仍然会在Linux命令行中输出信息

![image-20200515224201185](/md-img/image-20200515224201185.png)

我们不希望这样的信息输出，使用**nohup java -jar StuInfoShow.jar &**， 即可让jar后端运行且忽略输入输出

![image-20200515224309993](/md-img/image-20200515224309993.png)

截至现在项目发布GitHub、Linux部署成功

------

实际上，我们将源码下载到Linux无需打成jar包，直接通过maven 就可以启动项目

在项目根目录下执行命令：**nohup mvn spring-boot:run &**，在后端忽略输入输出的前提下，运行spring-boot项目

![image-20200515224406845](/md-img/image-20200515224406845.png)

# 第七节——实现其他API功能在前端页面：

自第五节实现简单的分页查询学生信息并显示之后，还需要按照需求利用已经实现并测试的后端API完善功能，即CRUD。

细化分析有以下关键功能：

1. 查找用户，根据id输入框的输入查找学生
2. 根据输入框和选择框信息点击添加学生信息
3. 根据输入框和选择框信息点击更新将id相同的学生信息更新】
4. 根据id输入框信息删除指定的学生信息
5. 为了方便删除和更新，要求在选择一个学生信息后，输入框的信息将自动填充

### part1：

要实现CRUD，首先需要一个输入区域，这里使用element-ui组件，简单定义一个输入div

```html
<div class="inputarea">
                <el-input class="xh" placeholder="请输入学号" v-model="inputxh" clearable>
                </el-input>
                <el-input class="name" placeholder="请输入姓名" v-model="inputname" clearable>
                </el-input>
                <template v-model="inputsex" class="sex">
                    <el-radio class="iconfont icon-nan1" v-model="inputsex" label="男">boy</el-radio>
                    <el-radio class="iconfont icon-nv1" v-model="inputsex" label="女">girl</el-radio>
                </template>
                <el-input class="className" placeholder="请输入班级" v-model="inputclassName" clearable>
                </el-input>
            </div>
```

其中placeholder是输入框为空时的提示信息，v-model用于数据绑定，在data中指定默认值

### part2：

要提交CRUD操作，需要一组button用于提交期望的事件，这里定义一个button div

```html
<div class="buttonarea">
                <el-button class="search" round :loading="loading_search" @click="selData()">查找</el-button>
                <el-button class="create" round :loading="loading_create" @click="creData()">添加</el-button>
                <el-button class="update" round :loading="loading_update" @click="upData">更新</el-button>
                <el-button class="delete" round :loading="loading_delete" @click="delData">删除</el-button>
            </div>
```

其中，round:loading是设定button点击之后、执行完毕之前显示一个加载状态，data中默认将这些设为false；@click绑定一个处理方法

完成part1和part2设计div后的data如下：

```javascript
data: function () {
            return {
                tableData: [],
                loading: true,
                inputxh: '',
                inputname: '',
                inputsex: '男',
                inputclassName: '',
                loading_create: false,
                loading_search: false,
                loading_update: false,
                loading_delete: false,
                xhData: ''
            }
        },
```

对应的CRUD点击事件处理方法：

```javascript
			selData() {
                var self = this;
                console.log('pre:' + self.inputxh);
                self.loading_search = true;
                axios({
                    methods: 'get',
                    url: 'StuInfo/xh?param=' + self.inputxh
                }).then(function (res) {
                    self.tableData = res.data;
                    self.loading_search = false;
                }).catch(function (error) {
                    console(error);
                });
                console.log('查找！！！！！！！！！！！！！');
            },
            creData() {
                var self = this;
                self.loading_create = true;
                axios.post('StuInfo', {
                    "xh": self.inputxh,
                    "name": self.inputname,
                    "sex": self.inputsex,
                    "className": self.inputclassName
                }).then(res => {
                    if (res.data == true) {
                        alert("添加成功!");
                    }
                    else {
                        console.log("添加失败！！！！！！");
                    }
                }).catch(error => {
                    console.log(error);
                    if (error == "Error: Request failed with status code 500")
                        alert("已有学号为" + self.inputxh + "的学生！");
                });
                self.loading_create = false;
                console.log('创建！！！！！！！！！！！！！');
            },
            upData() {
                var self = this;
                self.loading_update = true;
                axios.put('StuInfo', {
                    "xh": self.inputxh,
                    "name": self.inputname,
                    "sex": self.inputsex,
                    "className": self.inputclassName
                }).then(res => {
                    if (res.data == true) {
                        alert("更新成功!");
                    }
                    else {
                        alert("没有此学号的学生！");
                    }
                });
                self.loading_update = false;
                console.log('更新！！！！！！！！！！！！！');
            },
            delData() {
                var self = this;
                self.loading_delete = true;

                axios.delete('StuInfo?xh='+self.inputxh).then(res => {
                    if (res.data == true) {
                        alert("删除成功!");
                    }
                    else {
                        alert("没有此学号的学生！");
                    }
                }).catch(error => {
                    console.log("出错！" + error.respponse);
                });
                self.loading_delete = false;
                console.log('删除！！！！！！！！！！！！！');
            }
```

CRUD无非是通过axios使用一定的api发送ajax请求到服务器，处理响应的信息并更新页面

part3：

方便增改删，就要能根据当前选择的行自动填充数据到输入框中，这里用el-table的current-change事件来处理当前行变化

```javascript
handleCurretLineChange(val) {
                console.log(val);
                var self = this;
                self.inputxh = val.xh;
                self.inputname = val.name;
                self.inputsex = val.sex;
                self.inputclassName = val.className;
            },
```

在这个方法中，根据当前行的数据直接赋值到对应的输入框绑定的数据，即可实现点击当前行，其数据会填充到输入区

最终页面：

![image-20200529000610677](/md-img/image-20200529000610677.png)