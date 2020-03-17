/**
 * @program: mongoToExcle
 * @description: 多线程取款
 * @author: chenzhennan
 * @create: 2020-03-06 16:55
 **/
public class MoneyMain {
    public static void main(String[] args) {
        Bank bank = new Bank();
        PersonA personA = new PersonA(bank);
        PersonB personB = new PersonB(bank);
        personA.start();
        personB.start();
    }
}
