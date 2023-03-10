package github.oineh.monitoring.team.web.rest.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateIpRequest {

    private Long teamId;
    private String name;
    private int a;
    private int b;
    private int c;
    private int d;

    public String getIp() {
        valid();
        return a + "." + b + "." + c + "." + d;
    }

    private void valid() {
        if (ipSize(a) || ipSize(b) || ipSize(c) || ipSize(d)) {
            throw new IllegalArgumentException("IP 0~255 범위를 벗아났습니다.");
        }
    }

    public boolean ipSize(int num) {
        return !(num <= 255 && num >= 0);
    }
}
