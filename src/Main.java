import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
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

    /**
     * 以json字符串的方式展示树
     * @param treeItemList 树
     * @throws JsonProcessingException 异常
     */
    private void displayTree(List<TreeItem> treeItemList) throws JsonProcessingException {
        // 创建ObjectMapper实例
        ObjectMapper mapper = new ObjectMapper();
        // 将对象转换为JSON字符串
        String jsonString = mapper.writeValueAsString(treeItemList);
        // 输出JSON字符串
        System.out.println(jsonString);
    }

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
}