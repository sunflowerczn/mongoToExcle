/**
 * @program: mongoToExcle
 * @description: B人取款
 * @author: chenzhennan
 * @create: 2020-03-06 16:52
 **/
public class PersonB extends Thread {

    Bank bank;
    public PersonB(Bank bank){
        this.bank = bank;
    }

    @Override
    public void run() {
        while (Bank.totalMoney > 200){
            bank.ATM(200);
        }
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
