假设我们有一个咖啡订单系统，顾客可以根据自己的喜好选择不同类型的咖啡。
我们可以使用装饰器模式来为咖啡添加额外的配料，如牛奶、糖浆或奶泡。
装饰器模式允许我们动态地为咖啡对象添加不同的装饰，而无需修改原始咖啡类的代码。

装饰器模式

1.定义咖啡接口
首先定义一个咖啡接口，其中包含获取咖啡名称和计算价格的方法。

2.创建具体的咖啡类
实现咖啡接口的具体咖啡类，例如 Espresso、Latte 和 Cappuccino。

3.创建装饰器抽象类
定义一个装饰器抽象类，它实现了咖啡接口，并包含一个咖啡对象的引用。

4.创建具体的装饰器类
实现装饰器抽象类的具体装饰器类，用于添加额外的配料。例如，牛奶、糖浆和奶泡。

5.使用装饰器模式创建咖啡订单
在客户端代码中，可以使用装饰器模式创建咖啡订单，并动态地添加额外的配料。