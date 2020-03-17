/**
 * @program: mongoToExcle
 * @description: A人取款
 * @author: chenzhennan
 * @create: 2020-03-06 16:43
 **/
public class PersonA extends Thread {
    Bank bank;

    public PersonA(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void run() {
        while (Bank.totalMoney >= 100){
            bank.counter(100);
        }
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
