from datetime import datetime

from kivy.app import App
from kivy.clock import Clock
from kivy.core.window import Window
from kivy.metrics import sp
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label

try:
    from plyer import battery
except Exception:
    battery = None


class DigitalClockApp(App):
    title = "Digital Clock"

    def build(self):
        Window.clearcolor = (0, 0, 0, 1)
        try:
            Window.allow_screensaver = False
        except Exception:
            pass

        self.colors = [
            (0.0, 1.0, 1.0, 1.0),   # cyan
            (0.2, 1.0, 0.3, 1.0),   # green
            (1.0, 1.0, 0.1, 1.0),   # yellow
            (1.0, 0.2, 1.0, 1.0),   # magenta
            (1.0, 0.5, 0.1, 1.0),   # orange
            (1.0, 1.0, 1.0, 1.0),   # white
        ]
        self.color_index = 0

        layout = BoxLayout(
            orientation="vertical",
            padding=(20, 20),
            spacing=5
        )

        self.clock = Label(
            text="00:00:00",
            font_size=sp(72),
            bold=True,
            color=self.colors[0],
            halign="center",
            valign="middle"
        )
        self.clock.bind(size=self._sync_text_size)

        self.date = Label(
            text="",
            font_size=sp(22),
            color=(0.75, 0.75, 0.75, 1),
            halign="center",
            valign="middle"
        )
        self.date.bind(size=self._sync_text_size)

        self.battery = Label(
            text="",
            font_size=sp(18),
            color=(0.55, 0.55, 0.55, 1),
            halign="center",
            valign="middle"
        )
        self.battery.bind(size=self._sync_text_size)

        layout.add_widget(self.clock)
        layout.add_widget(self.date)
        layout.add_widget(self.battery)

        Clock.schedule_interval(self.update_clock, 0.2)
        return layout

    def _sync_text_size(self, widget, size):
        widget.text_size = size

    def update_clock(self, dt):
        now = datetime.now()

        self.clock.text = now.strftime("%H:%M:%S")
        self.date.text = now.strftime("%A  %d/%m/%Y")

        if battery:
            try:
                info = battery.status
                percent = info.get("percentage")
                if percent is not None:
                    self.battery.text = f"Battery: {int(percent)}%"
                else:
                    self.battery.text = ""
            except Exception:
                self.battery.text = ""
        else:
            self.battery.text = ""

        # تغيير اللون تقريبًا كل ثانية
        if now.microsecond < 250000:
            self.color_index = (self.color_index + 1) % len(self.colors)
            self.clock.color = self.colors[self.color_index]


if __name__ == "__main__":
    DigitalClockApp().run()
