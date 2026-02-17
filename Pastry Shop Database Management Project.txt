-- 1. CLEANUP (Safe & Silent)
BEGIN
-- Commit any pending transac􀆟on to avoid locks
COMMIT;
-- Drop Tables
FOR t IN (SELECT table_name FROM user_tables WHERE table_name IN (
'DIA_ORDER_DETAILS', 'DIA_ORDERS', 'DIA_PASTRY_INGREDIENTS',
'DIA_PURCHASES', 'DIA_CUSTOMERS', 'DIA_PASTRIES',
'DIA_INGREDIENTS', 'DIA_EMPLOYEES', 'DIA_SUPPLIERS', 'DIA_ORDERS_ARCHIVE'
)) LOOP
EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
END LOOP;
-- Drop Sequences
FOR s IN (SELECT sequence_name FROM user_sequences WHERE sequence_name IN
('SEQ_DIA_ORD', 'SEQ_DIA_CUST')) LOOP
EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
END LOOP;
-- Drop Views
FOR v IN (SELECT view_name FROM user_views WHERE view_name IN ('V_MENU',
'V_HIGH_VALUE_CLIENTS')) LOOP
EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
END LOOP;
-- Drop Synonyms
FOR syn IN (SELECT synonym_name FROM user_synonyms WHERE synonym_name =
'SYN_PASTRIES') LOOP
EXECUTE IMMEDIATE 'DROP SYNONYM ' || syn.synonym_name;
END LOOP;
EXCEPTION
WHEN OTHERS THEN NULL;
END;
/
-- 2. CREATE SEQUENCES
CREATE SEQUENCE SEQ_DIA_ORD START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE SEQ_DIA_CUST START WITH 1 INCREMENT BY 1;
-- 3. CREATE TABLES
-- Table: DIA_SUPPLIERS
CREATE TABLE DIA_SUPPLIERS (
supplier_id NUMBER(3) PRIMARY KEY,
supplier_name VARCHAR2(50) NOT NULL,
city VARCHAR2(30),
ra􀆟ng NUMBER(1) CHECK (ra􀆟ng BETWEEN 1 AND 5)
);
-- Table: DIA_EMPLOYEES
CREATE TABLE DIA_EMPLOYEES (
employee_id NUMBER(4) PRIMARY KEY,
last_name VARCHAR2(30) NOT NULL,
first_name VARCHAR2(30),
email VARCHAR2(50),
hire_date DATE DEFAULT SYSDATE,
job_id VARCHAR2(10),
salary NUMBER(8,2),
commission_pct NUMBER(2,2),
manager_id NUMBER(4) REFERENCES DIA_EMPLOYEES(employee_id)
);
-- Table: DIA_CUSTOMERS
-- Note: Email is VARCHAR2(50) here so we can ALTER it to 100 later.
CREATE TABLE DIA_CUSTOMERS (
customer_id NUMBER(3) PRIMARY KEY,
first_name VARCHAR2(30) NOT NULL,
last_name VARCHAR2(30) NOT NULL,
email VARCHAR2(50),
phone VARCHAR2(15),
address VARCHAR2(50),
city VARCHAR2(30),
credit_limit NUMBER(10,2),
crea􀆟on_date DATE
);
-- Table: DIA_PASTRIES
CREATE TABLE DIA_PASTRIES (
pastry_id NUMBER(3) PRIMARY KEY,
pastry_name VARCHAR2(30) NOT NULL,
category VARCHAR2(30),
price NUMBER(5,2),
availability VARCHAR2(10) CHECK (availability IN ('YES', 'NO', 'LTD')),
crea􀆟on_date DATE
);
-- Table: DIA_INGREDIENTS
CREATE TABLE DIA_INGREDIENTS (
ingredient_id NUMBER(3) PRIMARY KEY,
ingredient_name VARCHAR2(30),
unit_cost NUMBER(5,2),
stock_quan􀆟ty NUMBER(5)
);
-- Table: DIA_PASTRY_INGREDIENTS (Recipe Link Table)
CREATE TABLE DIA_PASTRY_INGREDIENTS (
pastry_id NUMBER(3),
ingredient_id NUMBER(3),
quan􀆟ty_needed NUMBER(3),
CONSTRAINT pk_pi PRIMARY KEY (pastry_id, ingredient_id),
CONSTRAINT 􀅅_pi_pastry FOREIGN KEY (pastry_id) REFERENCES DIA_PASTRIES(pastry_id),
CONSTRAINT 􀅅_pi_ingred FOREIGN KEY (ingredient_id) REFERENCES
DIA_INGREDIENTS(ingredient_id)
);
-- Table: DIA_ORDERS
CREATE TABLE DIA_ORDERS (
order_id NUMBER(4) PRIMARY KEY,
customer_id NUMBER(3) REFERENCES DIA_CUSTOMERS(customer_id),
employee_id NUMBER(4) REFERENCES DIA_EMPLOYEES(employee_id),
order_date DATE,
total_amount NUMBER(8,2),
status VARCHAR2(20) DEFAULT 'PENDING'
);
-- Table: DIA_ORDER_DETAILS
CREATE TABLE DIA_ORDER_DETAILS (
order_id NUMBER(4) REFERENCES DIA_ORDERS(order_id),
pastry_id NUMBER(3) REFERENCES DIA_PASTRIES(pastry_id),
quan􀆟ty NUMBER(3),
price_at_purchase NUMBER(5,2),
CONSTRAINT pk_ord_details PRIMARY KEY (order_id, pastry_id)
);
-- Table: DIA_PURCHASES
CREATE TABLE DIA_PURCHASES (
purchase_id NUMBER(4) PRIMARY KEY,
supplier_id NUMBER(3) REFERENCES DIA_SUPPLIERS(supplier_id),
ingredient_id NUMBER(3) REFERENCES DIA_INGREDIENTS(ingredient_id),
purchase_date DATE,
quan􀆟ty NUMBER(4),
cost_total NUMBER(8,2)
);
-- 4. DDL STATEMENTS (ALTER EXAMPLES - MIN 7)
-- DDL 1: Add constraint (Posi􀆟ve Salary)
ALTER TABLE DIA_EMPLOYEES ADD CONSTRAINT chk_salary_pos CHECK (salary > 0);
-- DDL 2: Add column (Loyalty Points)
ALTER TABLE DIA_CUSTOMERS ADD loyalty_points NUMBER(5) DEFAULT 0;
-- DDL 3: Modify column type/size (Expanding Email to 100 chars)
ALTER TABLE DIA_CUSTOMERS MODIFY email VARCHAR2(100);
-- DDL 4: Rename column
ALTER TABLE DIA_CUSTOMERS RENAME COLUMN address TO delivery_address;
-- DDL 5: Add unique constraint (Pastry Name must be unique)
ALTER TABLE DIA_PASTRIES ADD CONSTRAINT uq_pastry_name UNIQUE (pastry_name);
-- DDL 6: Create Index (Performance on Order Date)
CREATE INDEX idx_order_date ON DIA_ORDERS(order_date);
-- DDL 7: Create View (Simplified Menu View)
CREATE VIEW V_MENU AS SELECT pastry_name, price FROM DIA_PASTRIES WHERE availability =
'YES';
/*
==============================================================================
==
CHAPTER 2: DML - DATA MANIPULATION
==============================================================================
==
*/
----- INSERTS -----
-- 1. Suppliers
INSERT INTO DIA_SUPPLIERS VALUES (10, 'Sugar & Spice Co', 'Bucharest', 5);
INSERT INTO DIA_SUPPLIERS VALUES (20, 'Flour Power Ltd', 'Cluj', 4);
INSERT INTO DIA_SUPPLIERS VALUES (30, 'Dairy Kings', 'Brasov', 3);
-- 2. Employees
-- Context: 100 is the General Manager.
INSERT INTO DIA_EMPLOYEES VALUES (100, 'Popescu', 'Ion', 'ion.pop@mail.ro', TO_DATE('01-
01-2020','DD-MM-YYYY'), 'MNGR', 5000, NULL, NULL);
SELECT * FROM DIA_EMPLOYEES
-- MANDATORY STUDENT ROW
-- Name: Pahontu Diana-Ioana
INSERT INTO DIA_EMPLOYEES VALUES (101, 'Pahontu', 'Diana-Ioana', 'diana.pahontu@ase.ro',
SYSDATE, 'SALES', 2500, 0.1, 100);
SELECT * FROM DIA_EMPLOYEES WHERE employee_id = 101;
INSERT INTO DIA_EMPLOYEES VALUES (102, 'Ionescu', 'Maria', 'maria@mail.ro', TO_DATE('15-
03-2022','DD-MM-YYYY'), 'BAKER', 3000, NULL, 100);
INSERT INTO DIA_EMPLOYEES VALUES (103, 'Radu', 'Dan', 'dan@mail.ro', TO_DATE('20-05-
2023','DD-MM-YYYY'), 'SALES', 2400, 0.05, 100);
INSERT INTO DIA_EMPLOYEES VALUES (104, 'Dumitru', 'Elena', 'elena@mail.ro', TO_DATE('10-
10-2023','DD-MM-YYYY'), 'INTERN', 1500, NULL, 102);
SELECT * FROM DIA_EMPLOYEES
-- 3. Customers
-- Note: 'address' was renamed to 'delivery_address' in DDL, so we specify columns.
INSERT INTO DIA_CUSTOMERS (customer_id, first_name, last_name, email, city, credit_limit,
crea􀆟on_date)
VALUES (SEQ_DIA_CUST.NEXTVAL, 'Andrei', 'Mihalcea', 'andrei@yahoo.com', 'Bucharest', 1000,
TO_DATE('01-01-2024','DD-MM-YYYY'));
INSERT INTO DIA_CUSTOMERS (customer_id, first_name, last_name, email, city, credit_limit,
crea􀆟on_date)
VALUES (SEQ_DIA_CUST.NEXTVAL, 'George', 'Enescu', 'geo@music.ro', 'Iasi', 500, TO_DATE('15-
02-2024','DD-MM-YYYY'));
INSERT INTO DIA_CUSTOMERS (customer_id, first_name, last_name, email, city, credit_limit,
crea􀆟on_date)
VALUES (SEQ_DIA_CUST.NEXTVAL, 'Ana', 'Blandiana', 'ana@poet.ro', 'Cluj', 2000, TO_DATE('10-
03-2024','DD-MM-YYYY'));
SELECT * FROM DIA_CUSTOMERS
-- 4. Pastries
INSERT INTO DIA_PASTRIES VALUES (1, 'Croissant', 'Viennoiserie', 5.50, 'YES', SYSDATE-100);
INSERT INTO DIA_PASTRIES VALUES (2, 'Eclair Chocolate', 'Cake', 12.00, 'YES', SYSDATE-90);
INSERT INTO DIA_PASTRIES VALUES (3, 'Macarons Box', 'Cookies', 35.00, 'LTD', SYSDATE-80);
INSERT INTO DIA_PASTRIES VALUES (4, 'Fruit Tart', 'Tart', 15.00, 'NO', SYSDATE-50);
INSERT INTO DIA_PASTRIES VALUES (5, 'Bagel', 'Bread', 4.00, 'YES', SYSDATE-20);
SELECT * FROM DIA_PASTRIES
-- 5. Ingredients
INSERT INTO DIA_INGREDIENTS VALUES (500, 'Flour', 2.00, 100);
INSERT INTO DIA_INGREDIENTS VALUES (501, 'Bu􀆩er', 15.00, 50);
INSERT INTO DIA_INGREDIENTS VALUES (502, 'Sugar', 3.00, 200);
INSERT INTO DIA_INGREDIENTS VALUES (503, 'Chocolate', 25.00, 30);
SELECT * FROM DIA_INGREDIENTS
-- 6. Pastry Ingredients (Recipe Link)
INSERT INTO DIA_PASTRY_INGREDIENTS VALUES (1, 500, 100); -- Croissant uses Flour
INSERT INTO DIA_PASTRY_INGREDIENTS VALUES (1, 501, 50); -- Croissant uses Bu􀆩er
INSERT INTO DIA_PASTRY_INGREDIENTS VALUES (2, 503, 100); -- Eclair uses Chocolate
SELECT * FROM DIA_PASTRY_INGREDIENTS
-- 7. Orders
-- These link to Employee 101 (Pahontu Diana-Ioana), who was inserted above.
INSERT INTO DIA_ORDERS VALUES (SEQ_DIA_ORD.NEXTVAL, 1, 101, TO_DATE('01-04-2024','DDMM-
YYYY'), 55.00, 'COMPLETED');
INSERT INTO DIA_ORDERS VALUES (SEQ_DIA_ORD.NEXTVAL, 2, 103, TO_DATE('02-04-2024','DDMM-
YYYY'), 12.00, 'PENDING');
INSERT INTO DIA_ORDERS VALUES (SEQ_DIA_ORD.NEXTVAL, 1, 101, TO_DATE('05-04-2024','DDMM-
YYYY'), 35.00, 'COMPLETED');
INSERT INTO DIA_ORDERS VALUES (SEQ_DIA_ORD.NEXTVAL, 3, 101, SYSDATE, 0, 'NEW');
SELECT * FROM DIA_ORDERS
-- 8. Order Details
INSERT INTO DIA_ORDER_DETAILS VALUES (1000, 1, 10, 5.50); -- 10 Croissants
INSERT INTO DIA_ORDER_DETAILS VALUES (1001, 2, 1, 12.00); -- 1 Eclair
INSERT INTO DIA_ORDER_DETAILS VALUES (1002, 3, 1, 35.00); -- 1 Macaron Box
SELECT * FROM DIA_ORDER_DETAILS
-- 9. Purchases
INSERT INTO DIA_PURCHASES VALUES (1, 10, 502, SYSDATE-10, 50, 150.00); -- Sugar
INSERT INTO DIA_PURCHASES VALUES (2, 20, 500, SYSDATE-5, 100, 200.00); -- Flour
SELECT * FROM DIA_PURCHASES
----- OTHER DML OPERATIONS -----
-- UPDATE
-- Increase Cake prices by 10%
UPDATE DIA_PASTRIES SET price = price * 1.10 WHERE category = 'Cake';
SELECT * FROM DIA_PASTRIES WHERE category = 'Cake';
-- DELETE
-- Remove empty new orders
DELETE FROM DIA_ORDERS WHERE status = 'NEW' AND total_amount = 0;
-- MERGE (Mandatory)
-- Update stock based on purchases.
-- Using SUBQUERY to aggregate purchases prevents errors if mul􀆟ple purchases exist for same
item.
MERGE INTO DIA_INGREDIENTS i
USING (SELECT ingredient_id, SUM(quan􀆟ty) as quan􀆟ty FROM DIA_PURCHASES GROUP BY
ingredient_id) p
ON (i.ingredient_id = p.ingredient_id)
WHEN MATCHED THEN
UPDATE SET i.stock_quan􀆟ty = i.stock_quan􀆟ty + p.quan􀆟ty;
SELECT * FROM DIA_INGREDIENTS;
COMMIT;
/*
==============================================================================
==
CHAPTER 3: DML QUERIES (1-23)
==============================================================================
==
*/
-- 1. SIMPLE FILTERING (>, NOT LIKE, IS NULL)
-- Problem: Employees earning > 2000, not Managers, with commission.
SELECT first_name, last_name, salary, job_id
FROM DIA_EMPLOYEES
WHERE salary > 2000
AND job_id NOT LIKE 'MNGR' -- Using LIKE operator as requested
AND commission_pct IS NOT NULL;
-- 2. PATTERN MATCHING (LIKE)
-- Problem: Customers with Yahoo email or name star􀆟ng with 'A'.
SELECT first_name, last_name, email
FROM DIA_CUSTOMERS
WHERE email LIKE '%yahoo%' OR first_name LIKE 'A%';
-- 3. RANGES AND LISTS (BETWEEN, LIKE)
-- Problem: Pastries priced 5-20 OR are Cookies/Bread.
SELECT pastry_name, price, category
FROM DIA_PASTRIES
WHERE price BETWEEN 5 AND 20
OR (category LIKE 'Cookies' OR category LIKE 'Bread');
-- 4. MULTI-ROW COMPARISON (ANY)
-- Problem: Employees earning more than ANY Intern.
SELECT last_name, salary, job_id
FROM DIA_EMPLOYEES
WHERE salary > ANY (SELECT salary FROM DIA_EMPLOYEES WHERE job_id = 'INTERN');
-- 5. INNER JOIN
-- Problem: Order details with names and prices.
SELECT c.last_name, p.pastry_name, o.order_date, d.quan􀆟ty
FROM DIA_CUSTOMERS c, DIA_ORDERS o, DIA_ORDER_DETAILS d, DIA_PASTRIES p
WHERE c.customer_id = o.customer_id
AND o.order_id = d.order_id
AND d.pastry_id = p.pastry_id;
-- 6. OUTER JOIN
-- Problem: All pastries, even if unsold.
SELECT p.pastry_name, d.quan􀆟ty
FROM DIA_PASTRIES p, DIA_ORDER_DETAILS d
WHERE p.pastry_id = d.pastry_id(+);
-- 7. AGGREGATES & GROUP BY
-- Problem: Revenue per City.
SELECT c.city, SUM(o.total_amount) as total_revenue
FROM DIA_CUSTOMERS c, DIA_ORDERS o
WHERE c.customer_id = o.customer_id
GROUP BY c.city;
-- 8. FILTERING GROUPS (HAVING)
-- Problem: Suppliers with > 100 units sold.
SELECT s.supplier_name, SUM(p.quan􀆟ty)
FROM DIA_SUPPLIERS s, DIA_PURCHASES p
WHERE s.supplier_id = p.supplier_id
GROUP BY s.supplier_name
HAVING SUM(p.quan􀆟ty) >= 100;
-- 9. DATE FUNCTIONS
-- Problem: Orders in April, forma􀆩ed.
SELECT order_id, TO_CHAR(order_date, 'Day, DD Month YYYY') as f_date
FROM DIA_ORDERS
WHERE TO_CHAR(order_date, 'MM') = '04';
-- 10. STRING & NULL FUNCTIONS
-- Problem: Employee codes and compensa􀆟on.
SELECT last_name,
SUBSTR(last_name, 1, 3) || '_' || employee_id as emp_code,
salary + (salary * NVL(commission_pct, 0)) as total_comp
FROM DIA_EMPLOYEES;
-- 11. DECODE
-- Problem: Marke􀆟ng categories.
SELECT pastry_name, price,
DECODE(category, 'Viennoiserie', 'Breakfast', 'Cake', 'Luxury', 'Standard') as type
FROM DIA_PASTRIES;
-- 12. CASE STATEMENT
-- Problem: Risk profile based on credit limit.
SELECT last_name, credit_limit,
CASE
WHEN credit_limit < 1000 THEN 'Low'
WHEN credit_limit BETWEEN 1000 AND 2000 THEN 'Medium'
ELSE 'High'
END as risk
FROM DIA_CUSTOMERS;
-- 13. UNION
-- Problem: List all people (Employees + Customers).
SELECT first_name, last_name, email, 'Employee' as type FROM DIA_EMPLOYEES
UNION
SELECT first_name, last_name, email, 'Customer' as type FROM DIA_CUSTOMERS;
-- 14. MINUS
-- Problem: Ingredients never bought.
SELECT ingredient_id FROM DIA_INGREDIENTS
MINUS
SELECT ingredient_id FROM DIA_PURCHASES;
-- 15. SIMPLE SUBQUERY
-- Problem: Order for most expensive pastry.
SELECT order_id, quan􀆟ty
FROM DIA_ORDER_DETAILS
WHERE pastry_id = (SELECT pastry_id FROM DIA_PASTRIES WHERE price = (SELECT MAX(price)
FROM DIA_PASTRIES));
-- 16. CORRELATED SUBQUERY
-- Problem: Employees earning > job average.
SELECT e.last_name, e.salary, e.job_id
FROM DIA_EMPLOYEES e
WHERE e.salary > (SELECT AVG(e2.salary) FROM DIA_EMPLOYEES e2 WHERE e2.job_id =
e.job_id);
-- 17. HIERARCHICAL QUERY (UPDATED with SYS_CONNECT_BY_PATH)
-- Problem: Management tree showing the full path from top manager to employee.
SELECT LEVEL,
LPAD(' ', (LEVEL-1)*2) || last_name as tree,
SYS_CONNECT_BY_PATH(last_name, ' -> ') as management_path
FROM DIA_EMPLOYEES
START WITH manager_id IS NULL
CONNECT BY PRIOR employee_id = manager_id;
-- 18. EXTRACT
-- Problem: Tenure (Years).
SELECT last_name, EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM hire_date) as years
FROM DIA_EMPLOYEES;
-- 19. VIEWS
-- Problem: Query View V_MENU.
SELECT * FROM V_MENU WHERE price < 10;
-- 20. SYNONYMS
-- Problem: Use synonym.
CREATE SYNONYM SYN_PASTRIES FOR DIA_PASTRIES;
SELECT * FROM SYN_PASTRIES WHERE ROWNUM <= 3;
-- 21. CREATE TABLE AS SELECT
-- Problem: Archive old orders.
CREATE TABLE DIA_ORDERS_ARCHIVE AS
SELECT * FROM DIA_ORDERS WHERE order_date < SYSDATE;
-- 22. COMPLEX REPORT
-- Problem: Sales Report by Category.
SELECT p.category,
COUNT(DISTINCT o.order_id) as total_orders,
TO_CHAR(SUM(d.quan􀆟ty * d.price_at_purchase), '999,999.00') as revenue
FROM DIA_PASTRIES p, DIA_ORDER_DETAILS d, DIA_ORDERS o
WHERE p.pastry_id = d.pastry_id
AND d.order_id = o.order_id
GROUP BY p.category
ORDER BY revenue DESC;
-- 23. MANDATORY OPERATORS (!=, ALL)
-- Problem: Find employees who are NOT Interns and earn more than ALL Interns.
SELECT first_name, last_name, job_id, salary
FROM DIA_EMPLOYEES
WHERE job_id != 'INTERN'
AND salary > ALL (
SELECT salary FROM DIA_EMPLOYEES WHERE job_id = 'INTERN'
);
/*
==============================================================================
==
CHAPTER 4: EXTRA - FINANCIAL ANALYTICS
==============================================================================
==
*/
-- ANALYTIC 1: Sales Share % by Category
SELECT p.category,
ROUND((SUM(d.quan􀆟ty * d.price_at_purchase) /
(SELECT SUM(total_amount) FROM DIA_ORDERS)) * 100, 2) as sales_share_pct
FROM DIA_PASTRIES p, DIA_ORDER_DETAILS d, DIA_ORDERS o
WHERE p.pastry_id = d.pastry_id
AND d.order_id = o.order_id
GROUP BY p.category;
-- ANALYTIC 2: Best Selling Employees
SELECT e.last_name, COUNT(o.order_id) as orders_closed
FROM DIA_EMPLOYEES e, DIA_ORDERS o
WHERE e.employee_id = o.employee_id AND o.status = 'COMPLETED'
GROUP BY e.last_name
ORDER BY orders_closed DESC;
-- ANALYTIC 3: Stock Alert
SELECT i.ingredient_name, i.stock_quan􀆟ty
FROM DIA_INGREDIENTS i
WHERE i.stock_quan􀆟ty < 0.5 * (
SELECT MAX(p.quan􀆟ty) FROM DIA_PURCHASES p WHERE p.ingredient_id = i.ingredient_id
);
UPDATE DIA_INGREDIENTS SET stock_quan􀆟ty = 10 WHERE ingredient_name = 'Flour';
SELECT * FROM DIA_INGREDIENTS
--and run the command again
COMMIT;