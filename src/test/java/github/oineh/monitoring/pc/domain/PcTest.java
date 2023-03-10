package github.oineh.monitoring.pc.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("컴퓨터")
public class PcTest {

    @Test
    @DisplayName("PC 타입으로 만들기")
    public void create() {
        //given
        String name = "내컴퓨터";

        //when
        Pc pc = new Pc(name, Type.PC);

        //then
        assertThat(pc).isNotNull();
    }

    @Test
    @DisplayName("서버 타입으로 만들기")
    public void create2() {
        //given
        String name = "내 서버";

        //when
        Pc pc = new Pc(name, Type.SERVER);

        //then
        assertThat(pc).isNotNull();
    }
}
