/**
 * @program: mongoToExcle
 * @description: 多线程银行取款
 * @author: chenzhennan
 * @create: 2020-03-06 16:32
 **/
public class Bank {

    static Integer totalMoney = 10000;

    /**
     *通过柜台方式取款
     * @param money
     */
    public void counter(int money){
        Bank.totalMoney -= money;
        System.out.println("A取走了" + money + "元,账户余额为:" + Bank.totalMoney + "元");
    }

    /**
     * 通过ATM自助取款机取款
     * @param money
     */
    public void ATM(int money){
        Bank.totalMoney -= money;
        System.out.println("B取走了" + money + "元,账户余额为:" + Bank.totalMoney + "元");
    }

}
