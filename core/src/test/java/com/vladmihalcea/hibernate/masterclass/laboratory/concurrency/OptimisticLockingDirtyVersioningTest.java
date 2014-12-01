package com.vladmihalcea.hibernate.masterclass.laboratory.concurrency;

import com.vladmihalcea.hibernate.masterclass.laboratory.util.AbstractTest;
import org.hibernate.Session;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * OptimisticLockingDirtyVersioningTest - Test to check optimistic checking using the dirty properties instead of a synthetic version column
 *
 * @author Vlad Mihalcea
 */
public class OptimisticLockingDirtyVersioningTest extends AbstractTest {

    @Test
    public void testDirtyLockingProductOptimisticLocking() {

        LOGGER.info("testDirtyLockingProductOptimisticLocking");

        final DirtyPropertiesLockingProduct product = doInTransaction(new TransactionCallable<DirtyPropertiesLockingProduct>() {
            @Override
            public DirtyPropertiesLockingProduct execute(Session session) {
                DirtyPropertiesLockingProduct product = new DirtyPropertiesLockingProduct();
                product.setId(1L);
                product.setName("TV");
                product.setDescription("Plasma TV");
                product.setPrice(BigDecimal.valueOf(199.99));
                product.setQuantity(7L);
                session.persist(product);
                product.setQuantity(6L);
                return product;
            }
        });

        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                DirtyPropertiesLockingProduct _product = (DirtyPropertiesLockingProduct) session.get(DirtyPropertiesLockingProduct.class, 1L);
                _product.setPrice(BigDecimal.valueOf(21.22));
                LOGGER.info("Updating product price to {}", _product.getPrice());
                return null;
            }
        });

        product.setPrice(BigDecimal.ONE);
        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                LOGGER.info("Merging product");
                session.merge(product);
                session.flush();
                return null;
            }
        });

        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                DirtyPropertiesLockingProduct _product = (DirtyPropertiesLockingProduct) session.get(DirtyPropertiesLockingProduct.class, 1L);
                _product.setPrice(BigDecimal.valueOf(21.22));
                LOGGER.info("Updating product price to {}", _product.getPrice());
                return null;
            }
        });

        product.setPrice(BigDecimal.TEN);
        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                LOGGER.info("Reattaching product");
                session.saveOrUpdate(product);
                session.flush();
                return null;
            }
        });
    }

    @Test
    public void testProductOptimisticLocking() {

        LOGGER.info("testProductOptimisticLocking");

        final AllPropertiesLockingProduct product = doInTransaction(new TransactionCallable<AllPropertiesLockingProduct>() {
            @Override
            public AllPropertiesLockingProduct execute(Session session) {
                AllPropertiesLockingProduct product = new AllPropertiesLockingProduct();
                product.setId(1L);
                product.setName("TV");
                product.setDescription("Plasma TV");
                product.setPrice(BigDecimal.valueOf(199.99));
                product.setQuantity(7L);
                session.persist(product);
                product.setQuantity(6L);
                return product;
            }
        });

        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                AllPropertiesLockingProduct _product = (AllPropertiesLockingProduct) session.get(AllPropertiesLockingProduct.class, 1L);
                _product.setPrice(BigDecimal.valueOf(21.22));
                LOGGER.info("Updating product price to {}", _product.getPrice());
                return null;
            }
        });

        product.setPrice(BigDecimal.ONE);
        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                LOGGER.info("Merging product");
                session.merge(product);
                session.flush();
                return null;
            }
        });

        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                AllPropertiesLockingProduct _product = (AllPropertiesLockingProduct) session.get(AllPropertiesLockingProduct.class, 1L);
                _product.setPrice(BigDecimal.valueOf(21.22));
                LOGGER.info("Updating product price to {}", _product.getPrice());
                return null;
            }
        });

        product.setPrice(BigDecimal.TEN);
        doInTransaction(new TransactionCallable<Object>() {
            @Override
            public Object execute(Session session) {
                LOGGER.info("Reattaching product");
                session.saveOrUpdate(product);
                session.flush();
                return null;
            }
        });
    }

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                DirtyPropertiesLockingProduct.class,
                AllPropertiesLockingProduct.class
        };
    }

    @Entity(name = "DirtyPropertiesLockingProduct")
    @OptimisticLocking(type = OptimisticLockType.DIRTY)
    @DynamicUpdate
    @SelectBeforeUpdate(value = false)
    public static class DirtyPropertiesLockingProduct {

        @Id
        private Long id;

        @Column(unique = true, nullable = false)
        private String name;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false)
        private BigDecimal price;

        private long quantity;

        private int likes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public int getLikes() {
            return likes;
        }

        public int incrementLikes() {
            return ++likes;
        }
    }

    @Entity(name = "AllPropertiesLockingProduct")
    @OptimisticLocking(type = OptimisticLockType.ALL)
    @DynamicUpdate
    @SelectBeforeUpdate(value = true)
    public static class AllPropertiesLockingProduct {

        @Id
        private Long id;

        @Column(unique = true, nullable = false)
        private String name;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false)
        private BigDecimal price;

        private long quantity;

        private int likes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public int getLikes() {
            return likes;
        }

        public int incrementLikes() {
            return ++likes;
        }
    }
}