from conftest import DEFAULT_WAIT_SECONDS
from selenium.webdriver.support.wait import WebDriverWait


def test_user_cannot_access_admin_dashboard(logged_in_user_driver, base_url):
    wait = WebDriverWait(logged_in_user_driver, DEFAULT_WAIT_SECONDS)

    logged_in_user_driver.get(f"{base_url}/admin/dashboard")
    wait.until(
        lambda d: (
            "/dashboard" in d.current_url and "/admin/dashboard" not in d.current_url
        )
    )

    assert "/admin/dashboard" not in logged_in_user_driver.current_url
