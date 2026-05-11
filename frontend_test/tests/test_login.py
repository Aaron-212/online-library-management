from conftest import DEFAULT_WAIT_SECONDS
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait


def test_admin_can_login(logged_in_admin_driver):
    assert "/admin/dashboard" in logged_in_admin_driver.current_url


def test_user_can_login(logged_in_user_driver):
    assert "/dashboard" in logged_in_user_driver.current_url
    assert "/admin/dashboard" not in logged_in_user_driver.current_url


def test_login_no_name(normal_driver, base_url):
    wait = WebDriverWait(normal_driver, DEFAULT_WAIT_SECONDS)

    normal_driver.get(f"{base_url}/login")

    password_input = wait.until(EC.element_to_be_clickable((By.ID, "password")))
    password_input.clear()
    password_input.send_keys("123456")

    submit_button = wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))
    )
    submit_button.click()

    assert "/dashboard" not in normal_driver.current_url
    assert "/login" in normal_driver.current_url


def test_login_no_password(normal_driver, base_url):
    wait = WebDriverWait(normal_driver, DEFAULT_WAIT_SECONDS)

    normal_driver.get(f"{base_url}/login")

    username_input = wait.until(EC.element_to_be_clickable((By.ID, "username")))
    username_input.clear()
    username_input.send_keys("user01")

    submit_button = wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))
    )
    submit_button.click()

    assert "/dashboard" not in normal_driver.current_url
    assert "/login" in normal_driver.current_url
