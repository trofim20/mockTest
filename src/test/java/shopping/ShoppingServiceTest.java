package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

import static org.mockito.ArgumentMatchers.any;

/**
 * Тестирование интерфейса ShoppingService
 */
@ExtendWith(MockitoExtension.class)
public class ShoppingServiceTest {

    /**
     * Взаимодействие с БД для товаров
     */
    @Mock
    private ProductDao productDaoMock;

    /**
     * Сервис покупок
     */
    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    /**
     * Тестирование получения всех продуктов
     * Не нужно тестировать, так как внутри метода вызвается только метод мок объекта
     */
    @Test
    public void testGetAllProducts() {
    }

    /**
     * Тестирование получения товара по имени
     * Не нужно тестировать, так как внутри метода вызвается только метод мок объекта
     */
    @Test
    public void testGetProductByName() {
    }

    /**
     * Тестирование покупки при ситуации когда товаров хватает(не проходит)
     */
    @Test
    public void testBuy() throws Exception {
        Product product1 = new Product("Milk", 3);
        Product product2 = new Product("Tea", 3);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, 1);
        cart.add(product2, 2);

        boolean result = shoppingService.buy(cart);
        Assertions.assertTrue(result);
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product1);
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product2);
        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Тестирование покупки когда количество товаров в корзине равно количеству товаров в наличии(не проходит)
     */
    @Test
    public void testBuyWhenCountProductCartEqualStock() throws Exception {
        Product product1 = new Product("Milk", 3);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, 3);

        boolean result = shoppingService.buy(cart);
        Assertions.assertTrue(result);
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product1);
        Assertions.assertEquals(0, product1.getCount());
    }

    /**
     * Тестирование покупки товара, который закончился после добавления в корзину
     */
    @Test
    public void testBuyProductOutStockAfterAddCart() {
        Product product1 = new Product("Milk", 6);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, 5);
        product1.subtractCount(3);

        Exception exception = Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        Assertions.assertEquals("В наличии нет необходимого количества товара 'Milk'", exception.getMessage());
        Mockito.verify(productDaoMock, Mockito.never()).save(any());
    }

    /**
     * Тестирование покупки когда отрицательное количество товаров в корзине(не проходит)
     */
    @Test
    public void testBuyWithNegativeCountProduct() throws Exception {
        Product product1 = new Product("Milk", 3);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, -23);

        boolean result = shoppingService.buy(cart);
        Assertions.assertFalse(result);
        Mockito.verify(productDaoMock, Mockito.never()).save(any());
        Assertions.assertEquals(0, product1.getCount());
    }

    /**
     * Тестирование покупки когда корзина пустая
     */
    @Test
    public void testBuyWithCartEmpty() throws Exception {
        Cart cart = new Cart(new Customer(1, "11-11-11"));

        boolean result = shoppingService.buy(cart);
        Assertions.assertFalse(result);
        Mockito.verify(productDaoMock, Mockito.never()).save(any());
    }

    /**
     * Тестирование покупки когда нет корзины(не проходит)
     */
    @Test
    public void testBuyWithCartNull() throws Exception {
        Cart cart = null;

        boolean result = shoppingService.buy(cart);
        Assertions.assertFalse(result);
        Mockito.verify(productDaoMock, Mockito.never()).save(any());
    }

    /**
     * Тестирование выдачи корзины покупателя(не проходит)
     */
    @Test
    public void testGetCart() {
        Customer customer = new Customer(1, "11-11-11");
        Product product1 = new Product("Milk", 3);
        Product product2 = new Product("Tea", 2);
        Cart cart = shoppingService.getCart(customer);
        cart.add(product1, 1);
        cart.add(product2, 1);

        Cart newCart = shoppingService.getCart(customer);
        Assertions.assertEquals(newCart, cart);
        Assertions.assertEquals(newCart.getProducts().size(), cart.getProducts().size());
    }
}