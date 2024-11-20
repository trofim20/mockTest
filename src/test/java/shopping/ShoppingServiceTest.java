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

import java.util.List;
import java.util.Map;

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
     */
    @Test
    public void testGetAllProducts() {
        List<Product> products = List.of(
                new Product("Milk", 1),
                new Product("Tea", 1)
        );
        Mockito.when(productDaoMock.getAll()).thenReturn(products);

        List<Product> result = shoppingService.getAllProducts();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Milk", result.get(0).getName());
        Assertions.assertEquals("Tea", result.get(1).getName());
        Mockito.verify(productDaoMock, Mockito.times(1)).getAll();
    }

    /**
     * Тестирование получения товара по имени
     */
    @Test
    public void testGetProductByName() {
        Product product = new Product("Milk", 1);
        Mockito.when(productDaoMock.getByName("Milk")).thenReturn(product);

        Product result = shoppingService.getProductByName("Milk");
        Assertions.assertNotNull(product);
        Assertions.assertEquals("Milk", result.getName());
        Mockito.verify(productDaoMock, Mockito.times(1)).getByName("Milk");
    }

    /**
     * Тестирование покупки при ситуации когда товаров хватает и нехватает
     */
    @Test
    public void testBuy() throws Exception {
        Product product1 = new Product("Milk", 3);
        Product product2 = new Product("Tea", 1);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, 1);

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            cart.add(product2, 3);
        });
        Assertions.assertEquals("Невозможно добавить товар 'Tea' в корзину, т.к. нет необходимого количества товаров", exception.getMessage());

        boolean result = shoppingService.buy(cart);
        Assertions.assertTrue(result);
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product1);
    }

    /**
     * Тестирование проверки содержимого корзины покупателя
     */
    @Test
    public void testGetCart() {
        Product product1 = new Product("Milk", 3);
        Product product2 = new Product("Tea", 2);
        Cart cart = new Cart(new Customer(1, "11-11-11"));
        cart.add(product1, 1);
        cart.add(product2, 1);

        Map<Product, Integer> products = cart.getProducts();
        Assertions.assertTrue(products.containsKey(product1));
        Assertions.assertTrue(products.containsKey(product2));

    }
}