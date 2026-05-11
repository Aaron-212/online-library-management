import os

import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

DEFAULT_BASE_URL = "http://localhost:5173"
DEFAULT_WAIT_SECONDS = 10


def pytest_addoption(parser):
    parser.addoption(
        "--base-url",
        action="store",
        default=os.getenv("OLM_FRONTEND_BASE_URL", DEFAULT_BASE_URL),
        help="Frontend base URL for browser tests.",
    )
    parser.addoption(
        "--headless",
        action="store_true",
        default=os.getenv("OLM_HEADLESS", "false").lower() in {"1", "true", "yes"},
        help="Run the browser in headless mode when the selected driver supports it.",
    )


@pytest.fixture(scope="session")
def base_url(pytestconfig):
    return pytestconfig.getoption("--base-url").rstrip("/")


@pytest.fixture(scope="session")
def admin_credentials(pytestconfig):
    return {
        "username": "admin",
        "password": "123456",
        "role": "admin",
    }


@pytest.fixture(scope="session")
def user_credentials(pytestconfig):
    return {
        "username": "user01",
        "password": "123456",
        "role": "user",
    }


@pytest.fixture(scope="function")
def driver(pytestconfig):
    options = Options()

    if pytestconfig.getoption("--headless"):
        options.add_argument("--headless=new")

    browser = webdriver.Safari()

    try:
        if not pytestconfig.getoption("--headless"):
            browser.set_window_size(1440, 900)
        yield browser
    finally:
        browser.quit()


def login(driver, base_url, credentials):
    wait = WebDriverWait(driver, DEFAULT_WAIT_SECONDS)

    driver.get(f"{base_url}/login")

    username_input = wait.until(EC.element_to_be_clickable((By.ID, "username")))
    username_input.clear()
    username_input.send_keys(credentials["username"])

    password_input = wait.until(EC.element_to_be_clickable((By.ID, "password")))
    password_input.clear()
    password_input.send_keys(credentials["password"])

    submit_button = wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))
    )
    submit_button.click()

    wait.until(EC.url_contains("/dashboard"))

    return driver


@pytest.fixture(scope="function")
def normal_driver(driver):
    return driver


@pytest.fixture(scope="function")
def logged_in_admin_driver(driver, base_url, admin_credentials):
    return login(driver, base_url, admin_credentials)


@pytest.fixture(scope="function")
def logged_in_user_driver(driver, base_url, user_credentials):
    return login(driver, base_url, user_credentials)
