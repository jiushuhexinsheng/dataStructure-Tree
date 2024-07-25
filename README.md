# java Convert to tree-like data 常见的实体类转化为树结构类的几种方法
  在做菜单或者树状表格时经常会遇到将一般的数据或者实体类数据转化为树状结构数据的问题
  ，通常我们会使用递归的方式实现，但是递归会面临栈溢出的风险，并且速度也很慢。
  在网上我看了处理树的方法，一般分为两种，一种是递归的方式一种是非递归的方式，
  我自己也想了一种非递归的方式它通过模仿指针去转化，接下来我来分享这几个种方法 
## 1.首选创建一个实体类
```java    
    public class Item {
        private Integer id;
        private Integer pid;
        private String title;
    
        public Integer getId() {
            return id;
        }
    
        public void setId(Integer id) {
            this.id = id;
        }
    
        public Integer getPid() {
            return pid;
        }
    
        public void setPid(Integer pid) {
            this.pid = pid;
        }
    
        public String getTitle() {
            return title;
        }
    
        public void setTitle(String title) {
            this.title = title;
        }
    
        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", pid=" + pid +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
```
    
## 2.转化后的树结构类
```java   
    public class TreeItem {
        private Integer key;
        private String value;
        private Item data;
        private List<TreeItem> children;
        public Integer getKey() {
            return key;
        }
    
        public void setKey(Integer key) {
            this.key = key;
        }
    
        public String getValue() {
            return value;
        }
    
        public void setValue(String value) {
            this.value = value;
        }
    
        public Item getData() {
            return data;
        }
    
        public void setData(Item data) {
            this.data = data;
        }
    
        public List<TreeItem> getChildren() {
            return children;
        }
    
        public void setChildren(List<TreeItem> children) {
            this.children = children;
        }
    }
```
## 3.生成一个随机的实体类列表的方法
```java   
     /**
     * 生成一个随机的数据集合
     * @param num 集合树
     * @return 数据集合
     */
    private List<Item> generateList(int num) {
        List<Item> items = new ArrayList<Item>();
        for (int i = 1; i <= num; i++) {
            Item item = new Item();
            item.setId(i);
            if (items.isEmpty()){
                item.setTitle("数据");
                item.setPid(0);
                items.add(item);
                continue;
            }
            List<Integer> idsList = items.stream().map(Item::getId).toList();
            Random rand = new Random();
            int randomIndex = rand.nextInt(idsList.size());
            item.setTitle("数据");
            item.setPid(idsList.get(randomIndex));
            items.add(item);
            System.out.println(item);
        }
        return items;
    }
```
## 4.将实体类转化为树状结构类的方法
```java       
       /**
     * 将普通数据转化为树节点的数据
     * @param item 原始数据
     * @return 树节点的数据
     */
    private TreeItem dataConversion (Item item){
        TreeItem treeItem = new TreeItem();
        treeItem.setKey(item.getId());
        treeItem.setData(item);
        return treeItem;
    }
```
## 5.具体的方法
   ### 5.1 递归方法 
```java   
     /**
     * 使用递归的方法形成树
     * @param list 原始数组
     * @param treeItemList 成为树结构的数组
     */
    private void getRecursionGenerateTree(List<Item> list,List<TreeItem> treeItemList) {
        //查找顶点
        if (treeItemList.isEmpty()){
            list.forEach(item->{
                if (item.getPid() == 0){
                    treeItemList.add(dataConversion(item));
                }
            });
            getRecursionGenerateTree(list,treeItemList);
        }else {//非顶点进行循环插入
            treeItemList.forEach(item->{
                list.forEach(e->{
                    if (item.getKey().equals(e.getPid())){
                        if (item.getChildren()!=null){
                            item.getChildren().add(dataConversion(e));
                        }else {
                            item.setChildren(new ArrayList<>());
                            item.getChildren().add(dataConversion(e));
                        }

                    }
                });
                if (item.getChildren()!=null){
                    getRecursionGenerateTree(list,item.getChildren());
                }

            });
        }
    }
```    
   ### 5.2 非递归的方式生成树 --- 常用的方式
```java   
    /**
     * 使用非递归的方法形成树
     * @param list 原始数组
     * @param treeItemList 成为树结构的数组
     */
    private void getNotRecursionGenerateTree(List<Item> list,List<TreeItem> treeItemList) {

        //根据pid分组
        Map<Integer, List<TreeItem>> listMap = list.stream().collect(Collectors.groupingBy(Item::getPid,
                Collectors.mapping(this::dataConversion, Collectors.toList())));
        //查找根节点
        treeItemList.addAll(listMap.get(0));
        //将通过pid将节点相互连接
        listMap.forEach((k,l)->{
            if (l!=null){
                l.forEach(item->{
                    List<TreeItem> itemList = listMap.get(item.getKey());
                    if (itemList != null){
                        item.setChildren(itemList);
                    }
                });
            }
        });
    }
```
### 5.3 非递归的方式生成树 --- 模仿指针
```java   
    /**
     * 使用非递归的方法形成树 --- 模仿指针
     * @param list 原始数组
     * @param treeItemList 成为树结构的数组
     */
    private void getNowNotRecursionGenerateTree(List<Item> list,List<TreeItem> treeItemList) {
        //根据pid分组
        Map<Integer, List<TreeItem>> listMap = list.stream().collect(Collectors.groupingBy(Item::getPid,
                Collectors.mapping(this::dataConversion, Collectors.toList())));
        //查找根节点
        treeItemList.addAll(listMap.get(0));

        //当前数据
        TreeItem treeVo = null;
        //迭代器 == 带有指针
        Iterator<TreeItem> iterator = null;
        //需要循环的数据列表  === 能够记住需要循环的位置
        LinkedList<Iterator<TreeItem>> iteratorList = new LinkedList<>();
        iteratorList.add(treeItemList.iterator());
        //循环
        while (true){
            //将需要循环的数据拿出来
            iterator = iteratorList.getLast();
            //打破循环的条件
            if (iteratorList.size()==1 && !iterator.hasNext()){
                break;
            }
            //判断迭代器是否还有值
            if (!iterator.hasNext()){
                iteratorList.pollLast();
                continue;
            }
            //判断是否需要将节点相互连接
            treeVo = iterator.next();
            List<TreeItem> itemList = listMap.get(treeVo.getKey());
            if (itemList != null){
                treeVo.setChildren(itemList);
                iteratorList.push(itemList.iterator());
            }
        }  
    }
```    
## 6 最后进行测试
```java       
    public static void main(String[] args) throws JsonProcessingException {
        Main main = new Main();
        List<TreeItem> treeItemList = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要生成数组的个数：");

        //生成一个随机的原始数据数组
        List<Item> itemList = main.generateList(scanner.nextInt());

        //记录时间并生成树

        //递归的 方式
        long startTime = System.currentTimeMillis();
        main.getRecursionGenerateTree(itemList,treeItemList);
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        System.out.println("递归方式的运行时间：" + runtime + " 毫秒");



        //第一种方式
        startTime = System.currentTimeMillis();
        main.getNotRecursionGenerateTree(itemList,treeItemList);
        endTime = System.currentTimeMillis();
        runtime = endTime - startTime;
        System.out.println("非递归方式1的运行时间：" + runtime + " 毫秒");

        //第二种方式
        treeItemList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        main.getNowNotRecursionGenerateTree(itemList,treeItemList);
        endTime = System.currentTimeMillis();
        runtime = endTime - startTime;
        System.out.println("非递归方式2运行时间：" + runtime + " 毫秒");
    }
```
## 结果如下
### 1.当使用1000条数据进行测试时
    递归方式的运行时间：16 毫秒
    非递归方式1的运行时间：0 毫秒
    非递归方式2运行时间：0 毫秒
### 2.当使用10000条数据进行测试时
    递归方式的运行时间：395 毫秒
    非递归方式1的运行时间：15 毫秒
    非递归方式2运行时间：16 毫秒
### 3.当使用100000条数据进行测试时
    递归方式的运行时间：62595 毫秒
    非递归方式1的运行时间：63 毫秒
    非递归方式2运行时间：47 毫秒